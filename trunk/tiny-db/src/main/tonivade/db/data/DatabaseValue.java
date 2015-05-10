package tonivade.db.data;


public class DatabaseValue {

    private DataType type;

    private Object value;

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
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     *
     * @return
     * @throws NumberFormatException
     */
    public int incrementAndGet(int increment) throws NumberFormatException {
        int i = Integer.parseInt(value.toString()) + increment;
        this.value = String.valueOf(i);
        return i;
    }

    /**
     *
     * @return
     * @throws NumberFormatException
     */
    public int decrementAndGet(int decrement) throws NumberFormatException {
        int i = Integer.parseInt(value.toString()) - decrement;
        this.value = String.valueOf(i);
        return i;
    }

}
