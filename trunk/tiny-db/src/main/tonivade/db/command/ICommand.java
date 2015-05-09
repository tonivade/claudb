package tonivade.db.command;

import tonivade.db.data.Database;

public interface ICommand {

    public static final String OK = "OK";
    public static final String ERROR = "ERR";
    public static final String DELIMITER = "\r\n";

    public void execute(Database db, IRequest request, IResponse response);

}
