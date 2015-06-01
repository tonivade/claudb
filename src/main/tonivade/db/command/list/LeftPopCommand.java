package tonivade.db.command.list;

import java.util.List;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@ParamLength(1)
@ParamType(DataType.LIST)
public class LeftPopCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = db.get(request.getParam(0));
        if (value != null) {
            List<String> list = value.getValue();
            response.addBulkStr(list.remove(0));
        } else {
            response.addBulkStr(null);
        }
    }

}
