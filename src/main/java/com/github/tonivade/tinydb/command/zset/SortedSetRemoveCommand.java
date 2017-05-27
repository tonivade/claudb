/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.zset;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.zset;
import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.SortedSet;

@Command("zrem")
@ParamLength(2)
@ParamType(DataType.ZSET)
public class SortedSetRemoveCommand implements TinyDBCommand {

  @Override
  public RedisToken<?> execute(Database db, Request request) {
    List<SafeString> items =  request.getParams().stream().skip(1).collect(toList());
    List<SafeString> removed = new LinkedList<>();
    db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_ZSET,
             (oldValue, newValue) -> {
               Set<Entry<Double, SafeString>> merge = new SortedSet();
               merge.addAll(oldValue.getValue());
               for (SafeString item : items) {
                 if (merge.remove(item)) {
                   removed.add(item);
                 }
               }
               return zset(merge);
             });

    return RedisToken.integer(removed.size());
  }

}
