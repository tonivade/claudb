/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.zset;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.ITinyDBCommand;
import com.github.tonivade.tinydb.command.TinyDBResponse;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.IDatabase;

@ReadOnly
@Command("zrange")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetRangeCommand implements ITinyDBCommand {

  private static final String PARAM_WITHSCORES = "WITHSCORES";

  @Override
  public RedisToken execute(IDatabase db, IRequest request) {
    try {
      DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_ZSET);
      NavigableSet<Entry<Float, SafeString>> set = value.getValue();

      int from = Integer.parseInt(request.getParam(1).toString());
      if (from < 0) {
        from = set.size() + from;
      }
      int to = Integer.parseInt(request.getParam(2).toString());
      if (to < 0) {
        to = set.size() + to;
      }

      List<Object> result = emptyList();
      if (from <= to) {
        Optional<SafeString> withScores = request.getOptionalParam(3);
        if (withScores.isPresent() && withScores.get().toString().equalsIgnoreCase(PARAM_WITHSCORES)) {
          result = set.stream().skip(from).limit((to - from) + 1).flatMap(
              (o) -> Stream.of(o.getValue(), o.getKey())).collect(toList());
        } else {
          result = set.stream().skip(from).limit((to - from) + 1).map(
              (o) -> o.getValue()).collect(toList());
        }
      }

      return new TinyDBResponse().addArray(result);
    } catch (NumberFormatException e) {
      return RedisToken.error("ERR value is not an integer or out of range");
    }
  }

}
