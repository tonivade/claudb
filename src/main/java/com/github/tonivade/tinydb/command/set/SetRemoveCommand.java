/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.set;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;

import java.util.LinkedList;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseValue;

import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;

@Command("srem")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetRemoveCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    List<SafeString> items = Stream.ofAll(request.getParams()).tail().toList();
    LinkedList<SafeString> removed = new LinkedList<>();
    db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_SET,
        (oldValue, newValue) -> {
          Set<SafeString> merge = oldValue.getSet();
          items.retainAll(merge).forEach(removed::add);
          return set(merge.removeAll(items));
        });

    return integer(removed.size());
  }

}
