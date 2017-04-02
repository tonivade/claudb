/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.hash;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import java.util.Map;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;

import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.Database;

@ReadOnly
@Command("hvals")
@ParamLength(1)
@ParamType(DataType.HASH)
public class HashValuesCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, IRequest request) {
    DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_HASH);
    Map<SafeString, SafeString> map = value.getValue();
    return convert(map.values());
  }
}
