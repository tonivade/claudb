package com.github.tonivade.tinydb.command.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.TransactionState;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.TxIgnore;
import com.github.tonivade.tinydb.data.Database;

@Command("exec")
@TxIgnore
public class ExecCommand implements TinyDBCommand {

  @Override
  public RedisToken<?> execute(Database db, Request request) {
    Optional<TransactionState> transaction = getTransactionIfExists(request.getSession());
    if (transaction.isPresent()) {
      ITinyDB server = getTinyDB(request.getServerContext());
      List<RedisToken<?>> responses = new ArrayList<>();
      for (Request queuedRequest : transaction.get()) {
        responses.add(executeCommand(server, queuedRequest));
      }
      return RedisToken.array(responses);
    } else {
      return RedisToken.error("ERR EXEC without MULTI");
    }
  }

  private RedisToken<?> executeCommand(ITinyDB server, Request queuedRequest) {
    RespCommand command = server.getCommand(queuedRequest.getCommand());
    return command.execute(queuedRequest);
  }

  private Optional<TransactionState> getTransactionIfExists(Session session) {
    return session.removeValue("tx");
  }
}
