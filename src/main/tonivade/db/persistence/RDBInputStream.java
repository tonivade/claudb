/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;
import static tonivade.db.data.DatabaseValue.list;
import static tonivade.db.data.DatabaseValue.score;
import static tonivade.db.data.DatabaseValue.set;
import static tonivade.db.data.DatabaseValue.string;
import static tonivade.db.data.DatabaseValue.zset;
import static tonivade.db.persistence.Util.byteArrayToInt;
import static tonivade.db.redis.SafeString.safeString;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.CheckedInputStream;

import tonivade.db.data.Database;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.db.redis.SafeString;

public class RDBInputStream {

    private static final SafeString REDIS_PREAMBLE = safeString("REDIS");

    private static final int TWO_BYTES_LENGTH = 0x4000;
    private static final int ONE_BYTE_LENGTH = 0x40;

    private static final int HASH = 0x04;
    private static final int SORTED_SET = 0x03;
    private static final int SET = 0x02;
    private static final int LIST = 0x01;
    private static final int STRING = 0x00;

    private static final int TTL_MILISECONDS = 0xFC;
    private static final int TTL_SECONDS = 0xFD;
    private static final int SELECT = 0xFE;
    private static final int END_OF_STREAM = 0xFF;

    private static final int REDIS_VERSION = 6;
    private static final int VERSION_LENGTH = 4;
    private static final int REDIS_LENGTH = 5;

    private CheckedInputStream in;

    public RDBInputStream(InputStream in) {
        super();
        this.in = new CheckedInputStream(in, new CRC64());
    }

    public Map<Integer, IDatabase> parse() throws IOException {
        Map<Integer, IDatabase> databases = new HashMap<>();

        int version = version();

        if (version > REDIS_VERSION) {
            throw new IOException("invalid version: " + version);
        }

        IDatabase db = null;
        for (boolean end = false; !end;) {
            int read = in.read();
            switch (read) {
            case SELECT:
                // select db
                db = new Database();
                databases.put(parseLength(), db);
                break;
            case TTL_SECONDS:
                // TODO: TTL in seconds
                break;
            case TTL_MILISECONDS:
                // TODO: TTL in miliseconds
                break;
            case STRING:
                parseString(db);
                break;
            case LIST:
                parseList(db);
                break;
            case SET:
                parseSet(db);
                break;
            case SORTED_SET:
                parseSortedSet(db);
                break;
            case HASH:
                parseHash(db);
                break;
            case END_OF_STREAM:
                // end of stream
                end = true;
                db = null;
                break;
            default:
                throw new IOException("not supported: " + read);
            }
        }

        verifyChecksum();

        return databases;
    }

    private void verifyChecksum() throws IOException {
        long calculated = in.getChecksum().getValue();

        long readed = parseChecksum();

        if (calculated != readed) {
            throw new IOException("invalid checksum");
        }
    }

    private long parseChecksum() throws IOException {
        return Util.byteArrayToLong(read(Long.BYTES));
    }

    private int version() throws IOException {
        SafeString redis = new SafeString(read(REDIS_LENGTH));
        if (!redis.equals(REDIS_PREAMBLE)) {
            throw new IOException("not valid stream");
        }
        return parseVersion(read(VERSION_LENGTH));
    }

    private int parseVersion(byte[] version) {
        StringBuilder sb = new StringBuilder();
        for (byte b : version) {
            sb.append((char) b);
        }
        return Integer.parseInt(sb.toString());
    }

    private void parseString(IDatabase db) throws IOException {
        DatabaseKey key = parseKey();
        SafeString value = parseString();
        ensure(db, key, string(value));
    }

    private void parseList(IDatabase db) throws IOException {
        DatabaseKey key = parseKey();
        int size = parseLength();
        List<SafeString> list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            list.add(parseString());
        }
        ensure(db, key, list(list));
    }

    private void parseSet(IDatabase db) throws IOException {
        DatabaseKey key = parseKey();
        int size = parseLength();
        Set<SafeString> set = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            set.add(parseString());
        }
        ensure(db, key, set(set));
    }

    private void parseSortedSet(IDatabase db) throws IOException {
        DatabaseKey key = parseKey();
        int size = parseLength();
        Set<Entry<Double, SafeString>> entries = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            SafeString value = parseString();
            Double score = parseDouble();
            entries.add(score(score, value));
        }
        ensure(db, key, zset(entries));
    }

    private void parseHash(IDatabase db) throws IOException {
        DatabaseKey key = parseKey();
        int size = parseLength();
        Set<Entry<SafeString, SafeString>> entries = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            entries.add(entry(parseString(), parseString()));
        }
        ensure(db, key, hash(entries));
    }

    private void ensure(IDatabase db, DatabaseKey key, DatabaseValue value) throws IOException {
        if (db != null) {
            db.put(key, value);
        } else {
            throw new IOException("no database selected");
        }
    }

    private int parseLength() throws IOException {
        int length = in.read();
        if (length < ONE_BYTE_LENGTH) {
            // 1 byte: 00XXXXXX
            return length;
        } else if (length < TWO_BYTES_LENGTH) {
            // 2 bytes: 01XXXXXX XXXXXXXX
            int next = in.read();
            return parseLength(length, next);
        } else {
            // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
            return byteArrayToInt(read(Integer.BYTES));
        }
    }

    private int parseLength(int length, int next) {
        return ((length & 0x3F) << 8) | (next & 0xFF);
    }

    private SafeString parseString() throws IOException {
        int length = parseLength();
        return new SafeString(read(length));
    }

    private DatabaseKey parseKey() throws IOException {
        return DatabaseKey.safeKey(parseString());
    }

    private Double parseDouble() throws IOException {
        return Double.parseDouble(parseString().toString());
    }

    private byte[] read(int size) throws IOException {
        byte[] array = new byte[size];
        int read = in.read(array);
        if (read != size) {
            throw new IOException("error reading stream");
        }
        return array;
    }

}
