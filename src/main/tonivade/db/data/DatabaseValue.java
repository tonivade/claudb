package tonivade.db.data;

import java.io.Serializable;


public class DatabaseValue implements Serializable {

    private DataType type;

    private Serializable value;

    public DatabaseValue(DataType type) {
        this(type, null);
    }

    public DatabaseValue(DataType type, Serializable value) {
        this.type = type;
        this.value = value;
    }

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
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getValue() {
        return (T) value;
    }

    /**
     * @param value the value to set
     */
    public <T extends Serializable> void setValue(T value) {
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
