/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.hash;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.hash;

import java.util.LinkedList;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseValue;

import io.vavr.collection.List;
import io.vavr.collection.Map;

@Command("hdel")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashDeleteCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    List<SafeString> keys = List.ofAll(request.getParams()).tail();

    LinkedList<SafeString> removedKeys = new LinkedList<>();
    db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_HASH, (oldValue, newValue) -> {
      Map<SafeString, SafeString> merge = oldValue.getHash();
      for (SafeString key : keys) {
        merge.get(key).forEach(removedKeys::add);
        merge = merge.remove(key);
      }
      return hash(merge);
    });

    return integer(!removedKeys.isEmpty());
  }
}
