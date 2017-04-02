package com.github.tonivade.tinydb.command.key;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.Database;

@Command("persist")
@ParamLength(1)
public class PersistCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, IRequest request) {
    DatabaseKey key = db.overrideKey(safeKey(request.getParam(0)));
    return RedisToken.integer(key != null);
  }

}
