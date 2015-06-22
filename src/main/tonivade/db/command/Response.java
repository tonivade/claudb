/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import static tonivade.db.redis.SafeString.safeString;

import java.io.IOError;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tonivade.db.data.DatabaseValue;
import tonivade.db.redis.SafeString;

public class Response implements IResponse {

    private static final byte ARRAY = '*';
    private static final byte ERROR = '-';
    private static final byte INTEGER = ':';
    private static final byte SIMPLE_STRING = '+';
    private static final byte BULK_STRING = '$';

    private static final byte[] DELIMITER = new byte[] { '\r', '\n' };

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private boolean exit;

    private final ByteArrayBuilder builder = new ByteArrayBuilder();

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addValue(tonivade.db.data.DatabaseValue)
     */
    @Override
    public IResponse addValue(DatabaseValue value) {
        if (value != null) {
            switch (value.getType()) {
            case STRING:
                addBulkStr(value.getValue());
                break;
            case HASH:
                Map<String, String> map = value.getValue();
                List<Object> list = new LinkedList<>();
                map.forEach((k, v) ->  {
                    list.add(safeString(k));
                    list.add(safeString(v));
                });
                addArray(list);
                break;
            case LIST:
            case SET:
            case ZSET:
                addArray(value.getValue());
                break;
            default:
                break;
            }
        } else {
            addBulkStr(null);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addBulkStr(java.lang.String)
     */
    @Override
    public IResponse addBulkStr(SafeString str) {
        if (str != null) {
            builder.append(BULK_STRING).append(str.length()).append(DELIMITER).append(str);
        } else {
            builder.append(BULK_STRING).append(-1);
        }
        builder.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addSimpleStr(java.lang.String)
     */
    @Override
    public IResponse addSimpleStr(String str) {
        builder.append(SIMPLE_STRING).append(str);
        builder.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addInt(java.lang.String)
     */
    @Override
    public IResponse addInt(SafeString str) {
        builder.append(INTEGER).append(str);
        builder.append(DELIMITER);
        return this;
    }

    @Override
    public IResponse addInt(int value) {
        builder.append(INTEGER).append(value);
        builder.append(DELIMITER);
        return this;
    }

    @Override
    public IResponse addInt(boolean value) {
        builder.append(INTEGER).append(value ? "1" : "0");
        builder.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addError(java.lang.String)
     */
    @Override
    public IResponse addError(String str) {
        builder.append(ERROR).append(str);
        builder.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addArray(java.lang.String[])
     */
    @Override
    public IResponse addArrayValue(Collection<DatabaseValue> array) {
        if (array != null) {
            builder.append(ARRAY).append(array.size()).append(DELIMITER);
            for (DatabaseValue value : array) {
                addValue(value);
            }
        } else {
            builder.append(ARRAY).append(0).append(DELIMITER);
        }
        return this;
    }

    @Override
    public IResponse addArray(Collection<?> array) {
        if (array != null) {
            builder.append(ARRAY).append(array.size()).append(DELIMITER);
            for (Object value : array) {
                if (value instanceof Integer) {
                    addInt((Integer) value);
                } else if (value instanceof SafeString) {
                    addBulkStr((SafeString) value);
                } else if (value instanceof String) {
                    addSimpleStr((String) value);
                }
            }
        } else {
            builder.append(ARRAY).append(0).append(DELIMITER);
        }
        return this;
    }

    @Override
    public void exit() {
        this.exit = true;
    }

    @Override
    public boolean isExit() {
        return exit;
    }

    public byte[] getBytes() {
        return builder.build();
    }

    @Override
    public String toString() {
        try {
            return new String(getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IOError(e);
        }
    }

    private static class ByteArrayBuilder {

        private final ByteBuffer buffer = ByteBuffer.allocate(1024);

        public ByteArrayBuilder append(int i) {
            append(String.valueOf(i));
            return this;
        }

        public ByteArrayBuilder append(byte b) {
            buffer.put(b);
            return this;
        }

        public ByteArrayBuilder append(byte[] buf) {
            buffer.put(buf);
            return this;
        }

        public ByteArrayBuilder append(String str) {
            buffer.put(DEFAULT_CHARSET.encode(str));
            return this;
        }

        public ByteArrayBuilder append(SafeString str) {
            buffer.put(str.getBuffer());
            return this;
        }

        public byte[] build() {
            byte[] array = new byte[buffer.position()];
            buffer.rewind();
            buffer.get(array);
            return array;
        }

    }

}
