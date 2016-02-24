package tonivade.db.command.bitset;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.bitset;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;

@Command("setbit")
@ParamLength(3)
public class SetBitCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        int position = Integer.parseInt(request.getParam(1).toString());
        int value = Integer.parseInt(request.getParam(2).toString());
        Queue<Boolean> queue = new LinkedList<>();
        db.merge(safeKey(request.getParam(0)), bitset(), (oldValue, newValue) -> {
            BitSet bitSet = oldValue.<BitSet>getValue();
            queue.add(bitSet.get(position));
            bitSet.set(position, value != 0);
            return oldValue;
        });
        response.addInt(queue.poll());
    }

}
