package tonivade.db.command.impl;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

public class TimeCommand implements ICommand {

    private static final int SCALE = 1000;

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        long currentTimeMillis = Clock.systemDefaultZone().millis();
        List<String> result = Stream.of(
                seconds(currentTimeMillis), microseconds(currentTimeMillis)).collect(
                        Collectors.toList());
        response.addArray(result);
    }

    private String seconds(long currentTimeMillis) {
        return String.valueOf(currentTimeMillis / SCALE);
    }

    // XXX: Java doesn't have microsecond accuracy
    private String microseconds(long currentTimeMillis) {
        return String.valueOf((currentTimeMillis % SCALE) * SCALE);
    }
}
