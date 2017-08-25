package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

@Command("setnx")
@ParamLength(2)
public class SetIfNotExistsCommand implements TinyDBCommand {
  
  @Override
  public RedisToken execute(Database db, Request request) {
    DatabaseKey key = safeKey(request.getParam(0));
    DatabaseValue value = string(request.getParam(1));
    DatabaseValue savedValue = mergeValue(db, key, value);
    return integer(savedValue.equals(value));
  }

  private DatabaseValue mergeValue(Database db, DatabaseKey key, DatabaseValue value)
  {
    return db.merge(key, value, (oldValue, newValue) -> {
      if (oldValue.equals(DatabaseValue.EMPTY_STRING)) {
        return newValue;
      }
      return oldValue;
    });
  }
}
