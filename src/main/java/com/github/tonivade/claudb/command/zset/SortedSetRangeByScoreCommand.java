/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.zset;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.score;
import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.stream.Stream;

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
@Command("zrangebyscore")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetRangeByScoreCommand implements DBCommand {

  private static final String EXCLUSIVE = "(";
  private static final String MINUS_INFINITY = "-inf";
  private static final String INIFITY = "+inf";
  private static final String PARAM_WITHSCORES = "WITHSCORES";
  private static final String PARAM_LIMIT = "LIMIT";

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_ZSET);
      NavigableSet<Entry<Double, SafeString>> set = value.getSortedSet();

      float from = parseRange(request.getParam(1).toString());
      float to = parseRange(request.getParam(2).toString());

      Options options = parseOptions(request);

      Set<Entry<Double, SafeString>> range = set.subSet(
          score(from, SafeString.EMPTY_STRING), inclusive(request.getParam(1)),
          score(to, SafeString.EMPTY_STRING), inclusive(request.getParam(2)));

      List<Object> result = emptyList();
      if (from <= to) {
        if (options.withScores) {
          result = range.stream().flatMap(
              entry -> Stream.of(entry.getValue(), entry.getKey())).collect(toList());
        } else {
          result = range.stream().map(Entry::getValue).collect(toList());
        }

        if (options.withLimit) {
          result = result.stream().skip(options.offset).limit(options.count).collect(toList());
        }
      }

      return convert(result);
    } catch (NumberFormatException e) {
      return error("ERR value is not an float or out of range");
    }
  }

  private Options parseOptions(Request request) {
    Options options = new Options();
    for (int i = 3; i < request.getLength(); i++) {
      String param = request.getParam(i).toString();
      if (param.equalsIgnoreCase(PARAM_LIMIT)) {
        options.withLimit = true;
        options.offset = parseInt(request.getParam(++i).toString());
        options.count = parseInt(request.getParam(++i).toString());
      } else if (param.equalsIgnoreCase(PARAM_WITHSCORES)) {
        options.withScores = true;
      }
    }
    return options;
  }

  private boolean inclusive(SafeString param) {
    return !param.toString().startsWith(EXCLUSIVE);
  }

  private float parseRange(String param) {
    switch (param) {
    case INIFITY:
      return Float.MAX_VALUE;
    case MINUS_INFINITY:
      return Float.MIN_VALUE;
    default:
      if (param.startsWith(EXCLUSIVE)) {
        return Float.parseFloat(param.substring(1));
      }
      return Float.parseFloat(param);
    }
  }

  private static class Options {
    private boolean withScores;
    private boolean withLimit;
    private int offset;
    private int count;
  }
}
