package tonivade.db.command.impl;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

public class IncrementByCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseValue value = new DatabaseValue();
            value.setType(DataType.STRING);
            value.setValue("1");
            value = db.merge(request.getParam(0), value,
                    (oldValue, newValue) -> {
                        if (oldValue != null) {
                            int increment = Integer.parseInt(request.getParam(1));
                            oldValue.incrementAndGet(increment);
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
