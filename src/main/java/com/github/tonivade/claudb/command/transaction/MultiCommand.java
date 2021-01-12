/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.transaction;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.TransactionState;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.TxIgnore;
import com.github.tonivade.claudb.data.Database;

@Command("multi")
@TxIgnore
public class MultiCommand implements DBCommand {

  private static final String TRASACTION_KEY = "tx";

  @Override
  public RedisToken execute(Database db, Request request) {
    if (!isTxActive(request.getSession())) {
      createTransaction(request.getSession());
      return responseOk();
    } else {
      return error("ERR MULTI calls can not be nested");
    }
  }

  private void createTransaction(Session session) {
    session.putValue(TRASACTION_KEY, new TransactionState());
  }

  private boolean isTxActive(Session session) {
    return session.getValue(TRASACTION_KEY).isPresent();
  }
}
