/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import static tonivade.db.redis.SafeString.fromString;

import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    private boolean exit;

    private final ByteArrayBuilder sb = new ByteArrayBuilder();

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
                    list.add(fromString(k));
                    list.add(fromString(v));
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
            sb.append(BULK_STRING).append(str.length()).append(DELIMITER).append(str);
        } else {
            sb.append(BULK_STRING).append(-1);
        }
        sb.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addSimpleStr(java.lang.String)
     */
    @Override
    public IResponse addSimpleStr(String str) {
        sb.append(SIMPLE_STRING).append(str);
        sb.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addInt(java.lang.String)
     */
    @Override
    public IResponse addInt(SafeString str) {
        sb.append(INTEGER).append(str);
        sb.append(DELIMITER);
        return this;
    }

    @Override
    public IResponse addInt(int value) {
        sb.append(INTEGER).append(value);
        sb.append(DELIMITER);
        return this;
    }

    @Override
    public IResponse addInt(boolean value) {
        sb.append(INTEGER).append(value ? "1" : "0");
        sb.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addError(java.lang.String)
     */
    @Override
    public IResponse addError(String str) {
        sb.append(ERROR).append(str);
        sb.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addArray(java.lang.String[])
     */
    @Override
    public IResponse addArrayValue(Collection<DatabaseValue> array) {
        if (array != null) {
            sb.append(ARRAY).append(array.size()).append(DELIMITER);
            for (DatabaseValue value : array) {
                addValue(value);
            }
        } else {
            sb.append(ARRAY).append(0).append(DELIMITER);
        }
        return this;
    }

    @Override
    public IResponse addArray(Collection<?> array) {
        if (array != null) {
            sb.append(ARRAY).append(array.size()).append(DELIMITER);
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
            sb.append(ARRAY).append(0).append(DELIMITER);
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

    @Override
    public String toString() {
        return sb.toString();
    }

    private static class ByteArrayBuilder {

        private final ByteArrayOutputStream output = new ByteArrayOutputStream();

        public ByteArrayBuilder append(int i) {
            append(String.valueOf(i));
            return this;
        }

        public ByteArrayBuilder append(byte b) {
            output.write(b);
            return this;
        }

        public ByteArrayBuilder append(byte[] buf) {
            try {
                output.write(buf);
            } catch (IOException e) {
                throw new IOError(e);
            }
            return this;
        }

        public ByteArrayBuilder append(String str) {
            try {
                output.write(str.getBytes("UTF-8"));
            } catch (IOException e) {
                throw new IOError(e);
            }
            return this;
        }

        public ByteArrayBuilder append(SafeString str) {
            try {
                output.write(str.getBytes());
            } catch (IOException e) {
                throw new IOError(e);
            }
            return this;
        }

        @Override
        public String toString() {
            try {
                return output.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IOError(e);
            }
        }

    }

}
