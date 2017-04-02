/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command;

import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toList;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;
import static javaslang.Predicates.is;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.data.DatabaseValue;

public class TinyDBResponse {

  public RedisToken addValue(DatabaseValue value) {
    if (value != null) {
      switch (value.getType()) {
      case STRING:
          SafeString string = value.getValue();
          return RedisToken.string(string);
      case HASH:
      case ZSET:
          Map<SafeString, SafeString> map = value.getValue();
          return RedisToken.array(keyValueList(map));
      case LIST:
      case SET:
          Collection<SafeString> list = value.getValue();
          return addArray(list);
      default:
        break;
      }
    }
    return RedisToken.nullString();
  }

  public RedisToken addArray(Collection<?> array) {
    if (array == null) {
      return RedisToken.array();
    }
    return RedisToken.array(array.stream().map(this::parseToken).collect(toList()));
  }

  private RedisToken parseToken(Object value) {
    return Match(value).of(
        Case(instanceOf(Integer.class), RedisToken::integer),
        Case(instanceOf(Boolean.class), RedisToken::integer),
        Case(instanceOf(String.class), RedisToken::string),
        Case(instanceOf(Double.class), x -> string(x.toString())),
        Case(instanceOf(SafeString.class), RedisToken::string),
        Case(instanceOf(DatabaseValue.class), this::addValue),
        Case(instanceOf(RedisToken.class), Function.identity()),
        Case(is(null), x -> RedisToken.nullString()));
  }

  private List<RedisToken> keyValueList(Map<SafeString, SafeString> map) {
    return map.entrySet().stream()
        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
        .map(RedisToken::string)
        .collect(toList());
  }
}
