package tonivade.db.command;

import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.IDatabase;

public class CommandWrapper implements ICommand {

    private int params;

    private DataType dataType;

    private final ICommand command;

    public CommandWrapper(ICommand command) {
        this.command = command;
        ParamLength length = command.getClass().getAnnotation(ParamLength.class);
        if (length != null) {
            this.params = length.value();
        }
        ParamType type = command.getClass().getAnnotation(ParamType.class);
        if (type != null) {
            this.dataType = type.value();
        }
    }

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        if (request.getLength() < params) {
            response.addError("ERR wrong number of arguments for '" + request.getCommand() + "' command");
        } else if (!db.isType(request.getParam(0), dataType)) {
            response.addError("WRONGTYPE Operation against a key holding the wrong kind of value");
        } else {
            command.execute(db, request, response);
        }
    }

}
