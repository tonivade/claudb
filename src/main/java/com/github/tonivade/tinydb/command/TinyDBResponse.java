/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command;

import static java.util.stream.Collectors.toList;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Predicates.is;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.data.DatabaseValue;

class TinyDBResponse {

  static RedisToken convertValue(DatabaseValue value) {
    if (value != null) {
      switch (value.getType()) {
      case STRING:
          SafeString string = value.getValue();
          return RedisToken.string(string);
      case HASH:
          Map<SafeString, SafeString> map = value.getValue();
          return RedisToken.array(keyValueList(map));
      case LIST:
      case SET:
          Collection<SafeString> list = value.getValue();
          return convertArray(list);
      case ZSET:
          Set<Entry<Double, SafeString>> set = value.getValue();
          return convertArray(serialize(set));
      default:
        break;
      }
    }
    return RedisToken.nullString();
  }

  static RedisToken convertArray(Collection<?> array) {
    if (array == null) {
      return RedisToken.array();
    }
    return RedisToken.array(array.stream().map(TinyDBResponse::parseToken).collect(toList()));
  }

  private static RedisToken parseToken(Object value) {
    return Match(value).of(
        Case($(instanceOf(Integer.class)), RedisToken::integer),
        Case($(instanceOf(Boolean.class)), RedisToken::integer),
        Case($(instanceOf(String.class)), RedisToken::string),
        Case($(instanceOf(Double.class)), x -> RedisToken.string(x.toString())),
        Case($(instanceOf(SafeString.class)), RedisToken::string),
        Case($(instanceOf(DatabaseValue.class)), TinyDBResponse::convertValue),
        Case($(instanceOf(RedisToken.class)), Function.identity()),
        Case($(is(null)), ignore -> RedisToken.nullString()));
  }

  private static List<RedisToken> keyValueList(Map<SafeString, SafeString> map) {
    return map.entrySet().stream()
        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
        .map(RedisToken::string)
        .collect(toList());
  }

  private static Collection<?> serialize(Set<Entry<Double, SafeString>> set) {
    return set.stream()
        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue())).collect(toList());
  }
}
