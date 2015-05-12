package tonivade.db.command.impl;

import java.util.LinkedList;
import java.util.List;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

public class TimeCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<String> result = new LinkedList<>();
        long currentTimeMillis = System.currentTimeMillis();
        result.add(String.valueOf(currentTimeMillis/1000));
        result.add(String.valueOf(currentTimeMillis%1000));
        response.addArray(result);
    }

}
