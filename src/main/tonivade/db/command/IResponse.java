package tonivade.db.command;

import java.util.Collection;

import tonivade.db.data.DatabaseValue;

public interface IResponse {

    public IResponse addValue(DatabaseValue value);

    public IResponse addArrayValue(Collection<DatabaseValue> array);

    public IResponse addArray(Collection<String> array);

    public IResponse addBulkStr(String str);

    public IResponse addSimpleStr(String str);

    public IResponse addInt(String str);

    public IResponse addInt(int value);

    public IResponse addInt(boolean value);

    public IResponse addError(String str);


}