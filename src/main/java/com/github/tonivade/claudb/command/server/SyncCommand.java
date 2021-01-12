/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.server;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import java.io.IOException;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.persistence.ByteBufferOutputStream;
import com.github.tonivade.claudb.replication.MasterReplication;

@ReadOnly
@Command("sync")
public class SyncCommand implements DBCommand {

  private MasterReplication master;

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      DBServerContext server = getClauDB(request.getServerContext());

      ByteBufferOutputStream output = new ByteBufferOutputStream();
      server.exportRDB(output);

      if (master == null) {
        master = new MasterReplication(server);
        master.start();
      }

      master.addSlave(request.getSession().getId());

      return string(new SafeString(output.toByteArray()));
    } catch (IOException e) {
      return error("ERROR replication error");
    }
  }
}
