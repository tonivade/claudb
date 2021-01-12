/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.list;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@ReadOnly
@Command("lindex")
@ParamLength(2)
@ParamType(DataType.LIST)
public class ListIndexCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      ImmutableList<SafeString> list = db.getList(request.getParam(0));

      int index = Integer.parseInt(request.getParam(1).toString());
      if (index < 0) {
        index = list.size() + index;
      }

      // TODO: fix asArray
      return string(list.asArray().get(index));
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    } catch (IndexOutOfBoundsException e) {
      return nullString();
    }
  }
}
