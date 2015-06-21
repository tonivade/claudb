/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

import static tonivade.db.persistence.Util.toByteArray;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.zip.CheckedOutputStream;

import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

public class RDBOutputStream {

    private static final String DEFAULT_CHARSET = "UTF-8";

    // REDIS
    private static final byte[] REDIS = new byte[] {0x52, 0x45, 0x44, 0x49, 0x53};
    private static final int END_OF_STREAM = 0xFF;
    private static final int SELECT = 0xFE;

    private CheckedOutputStream out;

    public RDBOutputStream(OutputStream out) {
        super();
        this.out = new CheckedOutputStream(out, new CRC64());
    }

    public void preamble(int version) throws IOException {
        out.write(REDIS);
        out.write(version(version));
    }

    private byte[] version(int version) throws IOException {
        StringBuilder sb = new StringBuilder(String.valueOf(version));
        for (int i = sb.length(); i < Integer.BYTES; i++) {
            sb.insert(0, '0');
        }
        return sb.toString().getBytes(DEFAULT_CHARSET);
    }

    public void select(int db) throws IOException {
        out.write(SELECT);
        length(db);
    }

    public void dabatase(IDatabase db) throws IOException {
        for (Entry<String, DatabaseValue> entry : db.entrySet()) {
            value(entry.getKey(), entry.getValue());
        }
    }

    private void value(String key, DatabaseValue value) throws IOException {
        type(value.getType());
        key(key);
        value(value);
    }

    private void type(DataType type) throws IOException {
        out.write(type.ordinal());
    }

    private void key(String key) throws IOException {
        string(key);
    }

    private void value(DatabaseValue value) throws IOException {
        switch (value.getType()) {
        case STRING:
            string(value.getValue());
            break;
        case LIST:
            list(value.getValue());
            break;
        case HASH:
            hash(value.getValue());
            break;
        case SET:
            set(value.getValue());
            break;
        case ZSET:
            zset(value.getValue());
            break;
        default:
            break;
        }
    }

    private void length(int length) throws IOException {
        if (length < 0x40) {
            // 1 byte: 00XXXXXX
            out.write(length);
        } else if (length < 0x4000) {
            // 2 bytes: 01XXXXXX XXXXXXXX
            out.write(0x4000 & length);
        } else {
            // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
            out.write(0x80);
            out.write(toByteArray(length));
        }
    }

    private void string(String value) throws IOException {
        byte[] bytes = value.getBytes(DEFAULT_CHARSET);
        length(bytes.length);
        out.write(bytes);
    }

    private void string(double value) throws IOException {
        string(String.valueOf(value));
    }

    private void list(List<String> value) throws IOException {
        length(value.size());
        for (String item : value) {
            string(item);
        }
    }

    private void hash(Map<String, String> value) throws IOException {
        length(value.size());
        for (Entry<String, String> entry : value.entrySet()) {
            string(entry.getKey());
            string(entry.getValue());
        }
    }

    private void set(Set<String> value) throws IOException {
        length(value.size());
        for (String item : value) {
            string(item);
        }
    }

    private void zset(NavigableSet<Entry<Double, String>> value) throws IOException {
        length(value.size());
        for (Entry<Double, String> item : value) {
            string(item.getValue());
            string(item.getKey());
        }
    }

    public void end() throws IOException {
        out.write(END_OF_STREAM);
        out.write(toByteArray(out.getChecksum().getValue()));
        out.flush();
    }

}
