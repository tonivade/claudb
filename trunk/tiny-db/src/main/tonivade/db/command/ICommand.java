package tonivade.db.command;

import tonivade.db.data.IDatabase;

public interface ICommand {

    public static final String OK = "OK";
    public static final String ERROR = "ERR";

    public void execute(IDatabase db, IRequest request, IResponse response);

}
