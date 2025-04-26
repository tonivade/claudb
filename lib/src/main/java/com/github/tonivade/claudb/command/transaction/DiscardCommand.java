/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.transaction;

import com.github.tonivade.claudb.TransactionState;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.TxIgnore;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import java.util.Optional;

@Command("discard")
@TxIgnore
public class DiscardCommand implements DBCommand {

  private static final String TX_KEY = "tx";

  @Override
  public RedisToken execute(Database db, Request request) {
    removeTransactionIfExists(request.getSession());

    return RedisToken.responseOk();
  }

  private Optional<TransactionState> removeTransactionIfExists(Session session) {
    return session.removeValue(TX_KEY);
  }
}
