/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.TransactionState;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.TxIgnore;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;

@Command("exec")
@TxIgnore
public class ExecCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    Optional<TransactionState> transaction = getTransactionIfExists(request.getSession());
    if (transaction.isPresent()) {
      DBServerContext server = getClauDB(request.getServerContext());
      List<RedisToken> responses = new ArrayList<>();
      for (Request queuedRequest : transaction.get()) {
        responses.add(executeCommand(server, queuedRequest));
      }
      return RedisToken.array(responses);
    } else {
      return RedisToken.error("ERR EXEC without MULTI");
    }
  }

  private RedisToken executeCommand(DBServerContext server, Request queuedRequest) {
    RespCommand command = server.getCommand(queuedRequest.getCommand());
    return command.execute(queuedRequest);
  }

  private Optional<TransactionState> getTransactionIfExists(Session session) {
    return session.removeValue("tx");
  }
}
