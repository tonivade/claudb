/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.zset;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.score;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import java.util.Map.Entry;
import java.util.NavigableSet;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.data.SortedSet;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@Command("zincrby")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetIncrementByCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      DatabaseKey zkey = safeKey(request.getParam(0));
      DatabaseValue value = db.getOrDefault(zkey, DatabaseValue.EMPTY_ZSET);
      NavigableSet<Entry<Double, SafeString>> set = value.getSortedSet();

      SafeString key = request.getParam(2);
      Double increment = Double.parseDouble(request.getParam(1).toString());

      Entry<Double, SafeString> newValue = merge(set, key, increment);

      SortedSet result = new SortedSet();
      result.addAll(set);
      result.remove(newValue);
      result.add(newValue);
      db.put(zkey, zset(result));

      return string(newValue.getKey().toString());
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    }
  }

  private Entry<Double, SafeString>
          merge(NavigableSet<Entry<Double, SafeString>> set, SafeString key, Double increment) {
    return score(findByKey(set, key).getKey() + increment, key);
  }

  private Entry<Double, SafeString> findByKey(NavigableSet<Entry<Double, SafeString>> set, SafeString key) {
    // TODO: O(n) search, to fix forget the NavigableSet and use directly the SortedSet to get by key
    return set.stream().filter(entry -> entry.getValue().equals(key)).findFirst().orElse(score(0, key));
  }
}
