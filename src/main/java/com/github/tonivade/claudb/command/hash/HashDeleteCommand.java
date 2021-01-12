/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.hash;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.resp.protocol.RedisToken.integer;

import java.util.LinkedList;
import java.util.List;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.ImmutableMap;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@Command("hdel")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashDeleteCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    ImmutableList<SafeString> keys = request.getParams().asList().tail();

    List<SafeString> removedKeys = new LinkedList<>();
    db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_HASH, (oldValue, newValue) -> {
      ImmutableMap<SafeString, SafeString> merge = oldValue.getHash();
      for (SafeString key : keys) {
        merge.get(key).stream().forEach(removedKeys::add);
        merge = merge.remove(key);
      }
      return hash(merge);
    });

    return integer(!removedKeys.isEmpty());
  }
}
