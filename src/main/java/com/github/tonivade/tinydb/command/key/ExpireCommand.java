package com.github.tonivade.tinydb.command.key;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.Database;

@Command("expire")
@ParamLength(2)
public class ExpireCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      DatabaseKey key = db.overrideKey(safeKey(request.getParam(0), parsetTtl(request.getParam(1))));
      return RedisToken.integer(key != null);
    } catch (NumberFormatException e) {
      return RedisToken.error("ERR value is not an integer or out of range");
    }
  }

  private int parsetTtl(SafeString param) throws NumberFormatException {
    return Integer.parseInt(param.toString());
  }

}
