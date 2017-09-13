/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.set;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;

import java.util.LinkedList;
import java.util.Random;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseValue;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;

@ReadOnly
@Command("srandmember")
@ParamLength(1)
@ParamType(DataType.SET)
public class SetRandomMemberCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    LinkedList<SafeString> random = new LinkedList<>();
    db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_SET,
        (oldValue, newValue) -> {
          Array<SafeString> merge = oldValue.getSet().toArray();
          random.add(merge.get(random(merge)));
          return set(merge);
        });
    if (random.isEmpty()) {
      return RedisToken.nullString();
    } else {
      return RedisToken.string(random.get(0));
    }
  }

  private int random(Seq<?> merge) {
    return new Random().nextInt(merge.size());
  }

}
