/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.zset;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static java.util.Collections.emptyList;
import static java.util.Collections.reverse;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;

import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.Database;

@ReadOnly
@Command("zrevrange")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetReverseRangeCommand implements TinyDBCommand {

  private static final String PARAM_WITHSCORES = "WITHSCORES";

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_ZSET);
      NavigableSet<Entry<Float, SafeString>> set = value.getValue();

      int from = Integer.parseInt(request.getParam(2).toString());
      if (from < 0) {
        from = set.size() + from;
      }
      int to = Integer.parseInt(request.getParam(1).toString());
      if (to < 0) {
        to = set.size() + to;
      }

      List<Object> result = emptyList();
      if (from <= to) {
        Optional<SafeString> withScores = request.getOptionalParam(3);
        if (withScores.isPresent() && withScores.get().toString().equalsIgnoreCase(PARAM_WITHSCORES)) {
          result = set.stream().skip(from).limit((to - from) + 1l)
              .flatMap(item -> Stream.of(item.getValue(), item.getKey())).collect(toList());
        } else {
          result = set.stream().skip(from).limit((to - from) + 1l)
              .map(Entry::getValue).collect(toList());
        }
      }
      reverse(result);

      return convert(result);
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    }
  }
}
