package tonivade.db.command.set;

import java.util.Set;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("scard")
@ParamLength(1)
@ParamType(DataType.SET)
public class SetCardinalityCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = db.get(request.getParam(0));
        if (value != null) {
            Set<String> set = value.getValue();
            response.addInt(set.size());
        } else {
            response.addInt(0);
        }
    }

}
