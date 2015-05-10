package tonivade.db.command;

import java.util.Collection;

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
    public IResponse addBulkStr(Object str) {
        if (str != null) {
            sb.append(BULK_STRING).append(str.toString().length()).append(DELIMITER).append(str);
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
    public IResponse addSimpleStr(Object str) {
        sb.append(SIMPLE_STRING).append(str);
        sb.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addInt(java.lang.String)
     */
    @Override
    public IResponse addInt(Object str) {
        sb.append(INTEGER).append(str);
        sb.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addError(java.lang.String)
     */
    @Override
    public IResponse addError(Object str) {
        sb.append(ERROR).append(str);
        sb.append(DELIMITER);
        return this;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IResponse#addArray(java.lang.String[])
     */
    @Override
    public IResponse addArray(Collection<DatabaseValue> array) {
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
    public String toString() {
        return sb.toString();
    }

}
