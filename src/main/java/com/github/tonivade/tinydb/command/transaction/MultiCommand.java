package com.github.tonivade.tinydb.command.transaction;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.TransactionState;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.TxIgnore;
import com.github.tonivade.tinydb.data.Database;

@Command("multi")
@TxIgnore
public class MultiCommand implements TinyDBCommand {

  private static final String TRASACTION_KEY = "tx";

  @Override
  public RedisToken execute(Database db, IRequest request) {
    if (!isTxActive(request.getSession())) {
      createTransaction(request.getSession());
      return RedisToken.responseOk();
    } else {
      return RedisToken.error("ERR MULTI calls can not be nested");
    }
  }

  private void createTransaction(ISession session) {
    session.putValue(TRASACTION_KEY, new TransactionState());
  }

  private boolean isTxActive(ISession session) {
    return session.getValue(TRASACTION_KEY).isPresent();
  }

}
