/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.list;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import java.util.ArrayList;
import java.util.List;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.Database;

@Command("lset")
@ParamLength(3)
@ParamType(DataType.LIST)
public class ListSetCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, IRequest request) {
    try {
      int index = Integer.parseInt(request.getParam(1).toString());
      db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_LIST,
          (oldValue, newValue) -> {
            List<SafeString> merge = new ArrayList<>(oldValue.<List<SafeString>>getValue());
            merge.set(index > -1 ? index : merge.size() + index, request.getParam(2));
            return DatabaseValue.list(merge);
          });
      return RedisToken.status("OK");
    } catch (NumberFormatException e) {
      return RedisToken.error("ERR value is not an integer or out of range");
    } catch (IndexOutOfBoundsException e) {
      return RedisToken.error("ERR index out of range");
    }
  }

}
