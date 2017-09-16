/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.list;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.status;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.list;

import java.util.ArrayList;

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

@Command("lset")
@ParamLength(3)
@ParamType(DataType.LIST)
public class ListSetCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      int index = Integer.parseInt(request.getParam(1).toString());
      db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_LIST,
          (oldValue, newValue) -> {
            List<SafeString> oldList = oldValue.getValue();
            // TODO: use Array
            ArrayList<SafeString> array = new ArrayList<>(oldList.toJavaList());
            array.set(index > -1 ? index : array.size() + index, request.getParam(2));
            return list(array);
          });
      return status("OK");
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    } catch (IndexOutOfBoundsException e) {
      return error("ERR index out of range");
    }
  }
}
