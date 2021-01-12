/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.set;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.purefun.data.ImmutableArray;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@ReadOnly
@Command("srandmember")
@ParamLength(1)
@ParamType(DataType.SET)
public class SetRandomMemberCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    List<SafeString> random = new LinkedList<>();
    db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_SET,
        (oldValue, newValue) -> {
          ImmutableArray<SafeString> merge = oldValue.getSet().asArray();
          random.add(merge.get(random(merge)));
          return set(merge);
        });
    if (random.isEmpty()) {
      return nullString();
    } else {
      return string(random.get(0));
    }
  }

  private int random(Sequence<?> merge) {
    return new Random().nextInt(merge.size());
  }
}
