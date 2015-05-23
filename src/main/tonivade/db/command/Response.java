package tonivade.db.command;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tonivade.db.data.DatabaseValue;

public class Response implements IResponse {

    private static final String ARRAY = "*";
    private static final String ERROR = "-";
    private static final String INTEGER = ":";
    private static final String SIMPLE_STRING = "+";
    private static final String BULK_STRING = "$";

    private static final String DELIMITER = "\r\n";

    private final StringBuilder sb = new StringBuilder();

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
                List<String> list = new LinkedList<>();
                map.forEach((k, v) ->  {
                    list.add(k);
                    list.add(v);
                });
                addArray(list);
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
    public IResponse addBulkStr(String str) {
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
    public IResponse addInt(String str) {
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
    public IResponse addArray(Collection<String> array) {
        if (array != null) {
            sb.append(ARRAY).append(array.size()).append(DELIMITER);
            for (String value : array) {
                addBulkStr(value);
            }
        } else {
            sb.append(ARRAY).append(0).append(DELIMITER);
        }
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }

}
