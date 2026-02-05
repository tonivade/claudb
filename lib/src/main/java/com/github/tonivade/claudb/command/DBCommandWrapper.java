/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.status;

import com.github.tonivade.claudb.TransactionState;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.command.annotation.PubSubAllowed;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.command.annotation.TxIgnore;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import java.util.Optional;

public class DBCommandWrapper implements DBCommand {

  private int params;

  private DataType dataType;

  private final boolean pubSubAllowed;
  private final boolean txIgnore;
  private final boolean readOnly;

  private final DBCommand command;

  public DBCommandWrapper(DBCommand command) {
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
  public RedisToken execute(Database db, Request request) {
    // FIXME: ugly piece of code, please refactor
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
    return command.execute(db, request);
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

  private boolean isSubscribed(Request request) {
    return getSessionState(request.getSession()).isSubscribed();
  }
}
