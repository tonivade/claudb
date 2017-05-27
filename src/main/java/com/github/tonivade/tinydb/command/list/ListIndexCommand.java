/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.list;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import java.util.List;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.Database;

@ReadOnly
@Command("lindex")
@ParamLength(2)
@ParamType(DataType.LIST)
public class ListIndexCommand implements TinyDBCommand {

  @Override
  public RedisToken<?> execute(Database db, Request request) {
    try {
      DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_LIST);
      List<SafeString> list = value.getValue();

      int index = Integer.parseInt(request.getParam(1).toString());
      if (index < 0) {
        index = list.size() + index;
      }

      return RedisToken.string(list.get(index));
    } catch (NumberFormatException e) {
      return RedisToken.error("ERR value is not an integer or out of range");
    } catch (IndexOutOfBoundsException e) {
      return RedisToken.string(SafeString.EMPTY_STRING);
    }
  }

}
