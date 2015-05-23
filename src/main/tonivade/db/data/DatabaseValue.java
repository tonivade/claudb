package tonivade.db.data;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DatabaseValue {

    private DataType type;

    private Object value;

    public DatabaseValue(DataType type) {
        this(type, null);
    }

    public DatabaseValue(DataType type, Object value) {
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
    public <T> T getValue() {
        return (T) value;
    }

    /**
     * @param value the value to set
     */
    public <T> void setValue(T value) {
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

    public static DatabaseValue string(String value) {
        return new DatabaseValue(DataType.STRING, value);
    }

    public static DatabaseValue list(String ... values) {
        return new DatabaseValue(DataType.LIST,
                Stream.of(values).collect(Collectors.toCollection(LinkedList::new)));
    }

    public static DatabaseValue set(String ... values) {
        return new DatabaseValue(DataType.SET,
                Stream.of(values).collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    public static DatabaseValue zset(String ... values) {
        return new DatabaseValue(DataType.ZSET,
                Stream.of(values).collect(Collectors.toCollection(TreeSet::new)));
    }

    @SafeVarargs
    public static DatabaseValue hash(Entry<String, String> ... values) {
        return new DatabaseValue(
                DataType.HASH,
                Stream.of(values).collect(
                        Collectors.toMap(Entry::getKey, Entry::getValue)));
    }

    public static Entry<String, String> entry(String key, String value) {
        return new SimpleEntry<String, String>(key, value);
    }
}
