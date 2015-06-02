package tonivade.db.command.string;

import static tonivade.db.data.DatabaseValue.string;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("decrby")
@ParamLength(2)
@ParamType(DataType.STRING)
public class DecrementByCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseValue value = db.merge(request.getParam(0), string("-" + request.getParam(1)),
                    (oldValue, newValue) -> {
                        if (oldValue != null) {
                            int decrement = Integer.parseInt(request.getParam(1));
                            oldValue.decrementAndGet(decrement);
                            return oldValue;
                        }
                        return newValue;
                    });
            response.addInt(value.getValue());
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        }
    }

}
