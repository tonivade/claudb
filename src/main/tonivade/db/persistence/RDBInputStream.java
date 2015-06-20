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
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

public class RDBInputStream {

    private static final int HASH = 0x04;
    private static final int SORTED_SET = 0x03;
    private static final int SET = 0x02;
    private static final int LIST = 0x01;
    private static final int STRING = 0x00;
    private static final int TTL_MILISECONDS = 0xFC;
    private static final int TTL_SECONDS = 0xFD;
    private static final int SELECT = 0xFE;
    private static final int END_OF_STREAM = 0xFF;
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

        if (version > 6) {
            throw new IOException("invalid version: " + version);
        }

        for (boolean end = false; !end;) {
            IDatabase db = null;
            int read = in.read();
            switch (read) {
            case SELECT:
                // select db
                db = new Database(new HashMap<>());
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
        byte[] buf = new byte[Long.BYTES];
        in.read(buf);
        return Util.byteArrayToLong(buf);
    }

    private int version() throws IOException {
        byte[] redis = new byte[REDIS_LENGTH];
        in.read(redis);
        byte[] version = new byte[VERSION_LENGTH];
        in.read(version);
        return parseVersion(version);
    }

    private int parseVersion(byte[] version) {
        StringBuilder sb = new StringBuilder();
        for (byte b : version) {
            sb.append((char) b);
        }
        return Integer.parseInt(sb.toString());
    }

    private void parseString(IDatabase db) throws IOException {
        String key = parseString();
        String value = parseString();
        ensure(db, key, string(value));
    }

    private void parseList(IDatabase db) throws IOException {
        String key = parseString();
        int size = parseLength();
        List<String> list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            list.add(parseString());
        }
        ensure(db, key, list(list));
    }

    private void parseSet(IDatabase db) throws IOException {
        String key = parseString();
        int size = parseLength();
        Set<String> set = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            set.add(parseString());
        }
        ensure(db, key, set(set));
    }

    private void parseSortedSet(IDatabase db) throws IOException {
        String key = parseString();
        int size = parseLength();
        Set<Entry<Double, String>> entries = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            entries.add(score(parseDouble(), parseString()));
        }
        ensure(db, key, zset(entries));
    }

    private void parseHash(IDatabase db) throws IOException {
        String key = parseString();
        int size = parseLength();
        Set<Entry<String, String>> entries = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            entries.add(entry(parseString(), parseString()));
        }
        ensure(db, key, hash(entries));
    }

    private void ensure(IDatabase db, String key, DatabaseValue value) throws IOException {
        if (db != null) {
            db.put(key, value);
        } else {
            throw new IOException("no database selected");
        }
    }

    private int parseLength() throws IOException {
        int length = in.read();
        if (length < 0x40) {
            // 1 byte: 00XXXXXX
            return length;
        } else if (length < 0x4000) {
            // 2 bytes: 01XXXXXX XXXXXXXX
            int next = in.read();
            return ((length & 0x3F) << 8) | (next & 0xFF);
        } else {
            // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
            byte[] array = new byte[Integer.BYTES];
            in.read(array);
            return byteArrayToInt(array);
        }
    }

    private String parseString() throws IOException {
        int length = parseLength();
        byte[] buf = new byte[length];
        in.read(buf);
        return new String(buf, "UTF-8");
    }

    private Double parseDouble() throws IOException {
        return Double.parseDouble(parseString());
    }

}
