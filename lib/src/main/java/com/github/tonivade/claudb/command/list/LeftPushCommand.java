/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.list;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.list;
import static java.util.stream.Collectors.toList;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command("lpush")
@ParamLength(2)
@ParamType(DataType.LIST)
public class LeftPushCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    List<SafeString> values = request.getParamsAsStream().skip(1).collect(toList());
    Collections.reverse(values);

    DatabaseValue result = db.merge(safeKey(request.getParam(0)), list(values),
        (oldValue, newValue) -> {
          List<SafeString> merge = new ArrayList<>();
          merge.addAll(newValue.getList());
          merge.addAll(oldValue.getList());
          return list(merge);
        });

    return RedisToken.integer(result.size());
  }
}
