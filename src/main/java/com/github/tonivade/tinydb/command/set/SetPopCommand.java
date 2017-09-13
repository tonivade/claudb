/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.set;

import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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

import io.vavr.collection.Array;
import io.vavr.collection.Set;

@Command("spop")
@ParamLength(1)
@ParamType(DataType.SET)
public class SetPopCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    List<SafeString> removed = new LinkedList<>();
    db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_SET,
        (oldValue, newValue) -> {
          Set<SafeString> oldSet = oldValue.getSet();
          SafeString item = getRandomItem(oldSet);
          removed.add(item);
          return set(oldSet.remove(item));
        });
    if (removed.isEmpty()) {
      return nullString();
    } else {
      return string(removed.get(0));
    }
  }

  private SafeString getRandomItem(Set<SafeString> oldSet) {
    return oldSet.toArray().get(random(oldSet.toArray()));
  }

  private int random(Array<?> merge) {
    return new Random().nextInt(merge.size());
  }
}
