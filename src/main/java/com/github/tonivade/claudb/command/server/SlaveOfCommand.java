/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.server;

import static com.github.tonivade.resp.protocol.RedisToken.responseOk;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.replication.SlaveReplication;

@ReadOnly
@Command("slaveof")
@ParamLength(2)
public class SlaveOfCommand implements DBCommand {

  private SlaveReplication slave;

  @Override
  public RedisToken execute(Database db, Request request) {
    String host = request.getParam(0).toString();
    String port = request.getParam(1).toString();

    boolean stopCurrent = "NO".equals(host) && "ONE".equals(port);

    if (slave == null) {
      if (!stopCurrent) {
        startReplication(request, host, port);
      }
    } else {
      slave.stop();

      if (!stopCurrent) {
        startReplication(request, host, port);
      }
    }

    return responseOk();
  }

  private void startReplication(Request request, String host, String port) {
    slave = new SlaveReplication(
        getClauDB(request.getServerContext()), request.getSession(), host, Integer.parseInt(port));

    slave.start();
  }
}
