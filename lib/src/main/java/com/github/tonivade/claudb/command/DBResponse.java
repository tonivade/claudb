/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toList;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.stream.Stream;

class DBResponse {

  static RedisToken convertValue(DatabaseValue value) {
    if (value != null) {
      switch (value.getType()) {
      case STRING:
          SafeString string = value.getString();
          return string(string);
      case HASH:
          Map<SafeString, SafeString> map = value.getHash();
          return array(keyValueList(map));
      case LIST:
          List<SafeString> list = value.getList();
          return convertArray(list);
      case SET:
          Set<SafeString> set = value.getSet();
          return convertArray(set);
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
    if (value instanceof Integer) {
      return RedisToken.integer((Integer) value);
    }
    if (value instanceof Boolean) {
      return RedisToken.integer((Boolean) value);
    }
    if (value instanceof String) {
      return RedisToken.string((String) value);
    }
    if (value instanceof Double) {
      return RedisToken.string(value.toString());
    }
    if (value instanceof SafeString) {
      return RedisToken.string((SafeString) value);
    }
    if (value instanceof DatabaseValue) {
      return convertValue((DatabaseValue) value);
    }
    if (value instanceof RedisToken) {
      return (RedisToken) value;
    }
    return nullString();
  }

  private static List<RedisToken> keyValueList(Map<SafeString, SafeString> map) {
    return map.entrySet().stream()
        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
        .map(RedisToken::string).collect(toList());
  }

  private static Collection<?> serialize(NavigableSet<Entry<Double, SafeString>> set) {
    return set.stream()
        .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue())).collect(toList());
  }
}
