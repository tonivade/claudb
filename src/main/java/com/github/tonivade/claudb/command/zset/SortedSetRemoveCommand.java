/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.zset;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.score;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.SortedSet;

@Command("zrem")
@ParamLength(2)
@ParamType(DataType.ZSET)
public class SortedSetRemoveCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    List<SafeString> items =  request.getParams().stream().skip(1).collect(toList());
    List<SafeString> removed = new LinkedList<>();
    db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_ZSET,
             (oldValue, newValue) -> {
               Set<Entry<Double, SafeString>> merge = new SortedSet();
               merge.addAll(oldValue.getSortedSet());
               for (SafeString item : items) {
                 if (merge.remove(score(0, item))) {
                   removed.add(item);
                 }
               }
               return zset(merge);
             });

    return integer(removed.size());
  }
}
