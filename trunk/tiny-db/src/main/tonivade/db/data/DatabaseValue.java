package tonivade.db.data;


public class DatabaseValue {

    private DataType type;

    private String value;

    /**
     * @return the type
     */
    public DataType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(DataType type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     *
     * @return
     * @throws NumberFormatException
     */
    public int incrementAndGet() throws NumberFormatException {
        int i = Integer.parseInt(value);
        this.value = String.valueOf(++i);
        return i;
    }

}
