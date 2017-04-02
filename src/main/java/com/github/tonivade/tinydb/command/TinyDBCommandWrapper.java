/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.status;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import java.util.Optional;

import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IServerContext;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.TinyDBServerState;
import com.github.tonivade.tinydb.TinyDBSessionState;
import com.github.tonivade.tinydb.TransactionState;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.command.annotation.PubSubAllowed;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.command.annotation.TxIgnore;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.Database;

public class TinyDBCommandWrapper implements ICommand {

  private int params;

  private DataType dataType;

  private final boolean pubSubAllowed;
  private final boolean txIgnore;
  private final boolean readOnly;

  private final Object command;

  public TinyDBCommandWrapper(Object command) {
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
  public RedisToken execute(IRequest request) {
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
    if (command instanceof TinyDBCommand) {
      return executeTinyDBCommand(db, request);
    } else if (command instanceof ICommand) {
      return executeCommand(request);
    }
    return error("invalid command type: " + command.getClass());
  }

  private RedisToken executeCommand(IRequest request) {
    return ((ICommand) command).execute(request);
  }

  private RedisToken executeTinyDBCommand(Database db, IRequest request) {
    return ((TinyDBCommand) command).execute(db, request);
  }

  private void enqueueRequest(IRequest request) {
    getTransactionState(request.getSession()).ifPresent(tx -> tx.enqueue(request));
  }

  private boolean isTxActive(IRequest request) {
    return getTransactionState(request.getSession()).isPresent();
  }

  private Optional<TransactionState> getTransactionState(ISession session) {
    return session.getValue("tx");
  }

  private Database getCurrentDB(IRequest request) {
    TinyDBServerState serverState = getServerState(request.getServerContext());
    TinyDBSessionState sessionState = getSessionState(request.getSession());
    return serverState.getDatabase(sessionState.getCurrentDB());
  }

  private TinyDBServerState getServerState(IServerContext server) {
    return serverState(server).orElseThrow(() -> new IllegalStateException("missing server state"));
  }

  private TinyDBSessionState getSessionState(ISession session) {
    return sessionState(session).orElseThrow(() -> new IllegalStateException("missing session state"));
  }

  private Optional<TinyDBServerState> serverState(IServerContext server) {
    return server.getValue("state");
  }

  private Optional<TinyDBSessionState> sessionState(ISession session) {
    return session.getValue("state");
  }

  private boolean isSubscribed(IRequest request) {
    return getSessionState(request.getSession()).isSubscribed();
  }
}
