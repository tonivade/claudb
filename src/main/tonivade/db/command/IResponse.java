package tonivade.db.command;

import java.util.Collection;

import tonivade.db.data.DatabaseValue;

public interface IResponse {

    public IResponse addValue(DatabaseValue value);

    public IResponse addArray(Collection<DatabaseValue> array);

    public IResponse addBulkStr(Object str);

    public IResponse addSimpleStr(Object str);

    public IResponse addInt(Object str);

    public IResponse addError(Object str);


}