/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import static com.github.tonivade.purefun.Matcher1.instanceOf;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.stream.Stream;

import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.purefun.Pattern1;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.ImmutableMap;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

class DBResponse {

  static RedisToken convertValue(DatabaseValue value) {
    if (value != null) {
      switch (value.getType()) {
      case STRING:
          SafeString string = value.getString();
          return RedisToken.string(string);
      case HASH:
          ImmutableMap<SafeString, SafeString> map = value.getHash();
          return array(keyValueList(map).toList());
      case LIST:
          ImmutableList<SafeString> list = value.getList();
          return convertArray(list.toList());
      case SET:
          ImmutableSet<SafeString> set = value.getSet();
          return convertArray(set.toSet());
      case ZSET:
          NavigableSet<Entry<Double, SafeString>> zset = value.getSortedSet();
          return convertArray(serialize(zset));
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
    return RedisToken.array(array.stream().map(DBResponse::parseToken).collect(toList()));
  }

  private static RedisToken parseToken(Object value) {
    return Pattern1.<Object, RedisToken>build()
        .when(instanceOf(Integer.class))
          .then(integer -> RedisToken.integer((Integer) integer))
        .when(instanceOf(Boolean.class))
          .then(bool -> RedisToken.integer((Boolean) bool))
        .when(instanceOf(String.class))
          .then(string -> RedisToken.string((String) string))
        .when(instanceOf(Double.class))
          .then(double_ -> RedisToken.string(double_.toString()))
        .when(instanceOf(SafeString.class))
          .then(string -> RedisToken.string((SafeString) string))
        .when(instanceOf(DatabaseValue.class))
          .then(value_ -> DBResponse.convertValue((DatabaseValue) value_))
        .when(instanceOf(RedisToken.class))
          .then(token -> (RedisToken) token)
        .otherwise()
          .returns(nullString())
        .apply(value);
  }

  private static ImmutableList<RedisToken> keyValueList(ImmutableMap<SafeString, SafeString> map) {
    return ImmutableList.from(map.entries().stream()
        .flatMap(entry -> Stream.of(entry.get1(), entry.get2()))
        .map(RedisToken::string));
  }

  private static Collection<?> serialize(NavigableSet<Entry<Double, SafeString>> set) {
    return set.stream()
        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue())).collect(toList());
  }
}
