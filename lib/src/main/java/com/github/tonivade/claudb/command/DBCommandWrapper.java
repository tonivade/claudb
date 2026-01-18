/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.status;

import com.github.tonivade.claudb.DBServerState;
import com.github.tonivade.claudb.DBSessionState;
import com.github.tonivade.claudb.TransactionState;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.command.annotation.PubSubAllowed;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.command.annotation.TxIgnore;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.ServerContext;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import java.util.Optional;

public class DBCommandWrapper implements RespCommand {

  private int params;

  private DataType dataType;

  private final boolean pubSubAllowed;
  private final boolean txIgnore;
  private final boolean readOnly;

  private final Object command;

  public DBCommandWrapper(Object command) {
    this.command = command;
    ParamLength length = command.getClass().getAnnotation(ParamLength.class);
    if (length != null) {
      this.params = length.value();
    }
    ParamType type = command.getClass().getAnnotation(ParamType.class);
    if (type != null) {
      this.dataType = type.value();
    }
    this.readOnly = command.getClass().isAnnotationPresent(ReadOnly.class);
    this.txIgnore = command.getClass().isAnnotationPresent(TxIgnore.class);
    this.pubSubAllowed = command.getClass().isAnnotationPresent(PubSubAllowed.class);
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public boolean isTxIgnore() {
    return txIgnore;
  }

  public boolean isPubSubAllowed() {
    return pubSubAllowed;
  }

  @Override
  public RedisToken execute(Request request) {
    // FIXME: ugly piece of code, please refactor
    Database db = getCurrentDB(request);
    if (request.getLength() < params) {
      return error("ERR wrong number of arguments for '" + request.getCommand() + "' command");
    } else if (dataType != null && !db.isType(safeKey(request.getParam(0)), dataType)) {
      return error("WRONGTYPE Operation against a key holding the wrong kind of value");
    } else if (isSubscribed(request) && !pubSubAllowed) {
      return error("ERR only (P)SUBSCRIBE / (P)UNSUBSCRIBE / QUIT allowed in this context");
    } else if (isTxActive(request) && !txIgnore) {
      enqueueRequest(request);
      return status("QUEUED");
    }

    try {
      if (command instanceof DBCommand) {
        return executeDBCommand(db, request);
      } else if (command instanceof RespCommand) {
        return executeCommand(request);
      }
    } catch (CommandException e) {
      return error("ERR " + e.getMessage());
    }
    return error("invalid command type: " + command.getClass());
  }

  private RedisToken executeCommand(Request request) {
    return ((RespCommand) command).execute(request);
  }

  private RedisToken executeDBCommand(Database db, Request request) {
    return ((DBCommand) command).execute(db, request);
  }

  private void enqueueRequest(Request request) {
    getTransactionState(request.getSession()).ifPresent(tx -> tx.enqueue(request));
  }

  private boolean isTxActive(Request request) {
    return getTransactionState(request.getSession()).isPresent();
  }

  private Optional<TransactionState> getTransactionState(Session session) {
    return session.getValue("tx");
  }

  private Database getCurrentDB(Request request) {
    DBServerState serverState = getServerState(request.getServerContext());
    DBSessionState sessionState = getSessionState(request.getSession());
    return serverState.getDatabase(sessionState.getCurrentDB());
  }

  private DBServerState getServerState(ServerContext server) {
    return serverState(server).orElseThrow(() -> new IllegalStateException("missing server state"));
  }

  private DBSessionState getSessionState(Session session) {
    return sessionState(session).orElseThrow(() -> new IllegalStateException("missing session state"));
  }

  private Optional<DBServerState> serverState(ServerContext server) {
    return server.getValue("state");
  }

  private Optional<DBSessionState> sessionState(Session session) {
    return session.getValue("state");
  }

  private boolean isSubscribed(Request request) {
    return getSessionState(request.getSession()).isSubscribed();
  }
}
