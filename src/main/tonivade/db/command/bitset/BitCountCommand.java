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

@Command("bitcount")
@ParamLength(1)
@ParamType(DataType.BITSET)
public class BitCountCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue bitSet = db.getOrDefault(safeKey(request.getParam(0)), bitset());
        int count = bitSet.<BitSet>getValue().cardinality();
        response.addInt(count);
    }

}
