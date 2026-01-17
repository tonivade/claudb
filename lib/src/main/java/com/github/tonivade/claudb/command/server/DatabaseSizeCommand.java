/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.server;

import static com.github.tonivade.resp.protocol.RedisToken.integer;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;

@Command("dbsize")
public class DatabaseSizeCommand implements DBCommand {
  @Override
  public RedisToken execute(Database db, Request request) {
    return integer(db.size());
  }
}
