/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.data.DatabaseValue;

public class TinyDBResponse {
  private final IResponse response;

  public TinyDBResponse(IResponse response) {
    this.response = response;
  }

  public TinyDBResponse addValue(DatabaseValue value) {
    if (value != null) {
      switch (value.getType()) {
      case STRING:
        return RedisToken.string(value.getValue());
        break;
      case HASH:
        Map<SafeString, SafeString> map = value.getValue();
        return RedisToken.array(keyValueList(map));
        break;
      case LIST:
      case SET:
      case ZSET:
        return RedisToken.array(value.getValue());
        break;
      default:
        break;
      }
    } else {
      return RedisToken.string(null);
    }
    return this;
  }

  private List<SafeString> keyValueList(Map<SafeString, SafeString> map) {
    return map.entrySet().stream().flatMap((entry) -> Stream.of(entry.getKey(), entry.getValue())).collect(toList());
  }

  public TinyDBResponse addArrayValue(Collection<DatabaseValue> array) {
    if (array != null) {
      return RedisToken.array(array.stream().map(Optional::ofNullable).map(op -> op.isPresent() ? op.get().getValue() : null)
          .collect(toList()));
    } else {
      return RedisToken.array(null);
    }
    return this;
  }
}
