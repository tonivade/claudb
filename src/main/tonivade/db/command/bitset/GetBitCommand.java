package tonivade.db.command.bitset;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.bitset;

import java.util.BitSet;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.protocol.SafeString;

@Command("setbit")
@ParamLength(2)
@ParamType(DataType.STRING)
public class GetBitCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            int offset = Integer.parseInt(request.getParam(1).toString());
            DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), bitset());
            BitSet bitSet = BitSet.valueOf(value.<SafeString>getValue().getBuffer());
            response.addInt(bitSet.get(offset));
        } catch (NumberFormatException e) {
            response.addError("bit offset is not an integer");
        }
    }

}
