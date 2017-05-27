/*
 * Copyright (c) 2016-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.bitset;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.bitset;

import java.util.BitSet;

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

@Command("getbit")
@ParamLength(2)
@ParamType(DataType.STRING)
public class GetBitCommand implements TinyDBCommand {

  @Override
  public RedisToken<?> execute(Database db, Request request) {
    try {
      int offset = Integer.parseInt(request.getParam(1).toString());
      DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), bitset());
      BitSet bitSet = BitSet.valueOf(value.<SafeString>getValue().getBuffer());
      return integer(bitSet.get(offset));
    } catch (NumberFormatException e) {
      return error("bit offset is not an integer");
    }
  }

}
