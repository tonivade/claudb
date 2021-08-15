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
