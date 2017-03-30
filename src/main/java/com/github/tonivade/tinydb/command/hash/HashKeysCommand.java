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
import com.github.tonivade.tinydb.command.ITinyDBCommand;
import com.github.tonivade.tinydb.command.TinyDBResponse;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.IDatabase;

@ReadOnly
@Command("hkeys")
@ParamLength(1)
@ParamType(DataType.HASH)
public class HashKeysCommand implements ITinyDBCommand {

  @Override
  public RedisToken execute(IDatabase db, IRequest request) {
    DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_HASH);
    Map<SafeString, SafeString> map = value.getValue();
    return new TinyDBResponse().addSafeArray(map.keySet());
  }

}
