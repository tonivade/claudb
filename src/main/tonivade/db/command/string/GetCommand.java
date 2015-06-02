package tonivade.db.command.string;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.IDatabase;

@Command("get")
@ParamLength(1)
@ParamType(DataType.STRING)
public class GetCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        response.addValue(db.get(request.getParam(0)));
    }

}
