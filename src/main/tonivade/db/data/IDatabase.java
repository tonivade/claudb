package tonivade.db.data;

import java.util.Map;

public interface IDatabase extends Map<String, DatabaseValue> {

    public boolean rename(String from, String to);

    public boolean isType(String key, DataType type);

}
