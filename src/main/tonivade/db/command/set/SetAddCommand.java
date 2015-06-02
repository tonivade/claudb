package tonivade.db.command.set;

import static tonivade.db.data.DatabaseValue.set;

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

@Command("sadd")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetAddCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = db.merge(request.getParam(0), set(request.getParam(1)), (oldValue, newValue)-> {
            if (oldValue != null) {
                Set<String> oldSet = oldValue.getValue();
                Set<String> newSet = newValue.getValue();
                oldSet.addAll(newSet);
                return oldValue;
            }
            return newValue;
        });
        response.addInt(value.<Set<String>>getValue().size());
    }

}
