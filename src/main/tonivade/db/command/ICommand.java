package tonivade.db.command;

import tonivade.db.data.IDatabase;

public interface ICommand {

    public static final String RESULT_OK = "OK";
    public static final String RESULT_ERROR = "ERR";

    public void execute(IDatabase db, IRequest request, IResponse response);

}
