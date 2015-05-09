package tonivade.db.command;

import java.util.Collection;

import tonivade.db.data.DatabaseValue;

public interface IResponse {

    public abstract IResponse addValue(DatabaseValue value);

    public abstract IResponse addBulkStr(String str);

    public abstract IResponse addSimpleStr(String str);

    public abstract IResponse addInt(String str);

    public abstract IResponse addInt(int i);

    public abstract IResponse addInt(boolean b);

    public abstract IResponse addError(String str);

    public abstract IResponse addArray(Collection<DatabaseValue> array);

}