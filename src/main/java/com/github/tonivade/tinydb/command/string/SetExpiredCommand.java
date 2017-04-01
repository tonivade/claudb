package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.ITinyDBCommand;
import com.github.tonivade.tinydb.data.IDatabase;

@Command("setex")
@ParamLength(3)
public class SetExpiredCommand implements ITinyDBCommand {

  @Override
  public RedisToken execute(IDatabase db, IRequest request) {
    try {
      db.put(safeKey(request.getParam(0), parseTtl(request.getParam(1))), string(request.getParam(2)));
      return RedisToken.responseOk();
    } catch (NumberFormatException e) {
      return RedisToken.error("ERR value is not an integer or out of range");
    }
  }

  private int parseTtl(SafeString safeString) throws NumberFormatException {
    return Integer.parseInt(safeString.toString());
  }

}
