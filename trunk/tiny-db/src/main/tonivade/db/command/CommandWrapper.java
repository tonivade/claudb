package tonivade.db.command;

import tonivade.db.data.IDatabase;

public class CommandWrapper implements ICommand {

    private int params;

    private ICommand command;

    public CommandWrapper(ICommand command, int params) {
        this.command = command;
        this.params = params;
    }

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        if (request.getLength() < params) {
            response.addError("ERR wrong number of arguments for '" + request.getCommand() + "' command");
        } else {
            command.execute(db, request, response);
        }
    }

}
