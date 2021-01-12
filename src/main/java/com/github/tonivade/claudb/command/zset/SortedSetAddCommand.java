/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.zset;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.score;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static java.lang.Float.parseFloat;
import static java.util.stream.Collectors.toList;

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

@Command("zadd")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetAddCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      DatabaseValue initial = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_ZSET);
      DatabaseValue result = db.merge(safeKey(request.getParam(0)), parseInput(request),
          (oldValue, newValue) -> {
            Set<Entry<Double, SafeString>> merge = new SortedSet();
            merge.addAll(oldValue.getSortedSet());
            merge.addAll(newValue.getSortedSet());
            return zset(merge);
          });
      return integer(changed(initial.getSortedSet(), result.getSortedSet()));
    } catch (NumberFormatException e) {
      return error("ERR value is not a valid float");
    }
  }

  private int changed(Set<Entry<Double, SafeString>> input, Set<Entry<Double, SafeString>> result) {
    return result.size() - input.size();
  }

  private DatabaseValue parseInput(Request request) {
    Set<Entry<Double, SafeString>> set = new SortedSet();
    SafeString score = null;
    for (SafeString string : request.getParams().stream().skip(1).collect(toList())) {
      if (score != null) {
        set.add(score(parseFloat(score.toString()), string));
        score =  null;
      } else {
        score = string;
      }
    }
    return zset(set);
  }
}
