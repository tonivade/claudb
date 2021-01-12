/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.zset;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;

import java.util.Map.Entry;
import java.util.Set;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.data.Database;

@ReadOnly
@Command("zcard")
@ParamLength(1)
@ParamType(DataType.ZSET)
public class SortedSetCardinalityCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_ZSET);
    Set<Entry<Double, SafeString>> set = value.getSortedSet();
    return integer(set.size());
  }
}
