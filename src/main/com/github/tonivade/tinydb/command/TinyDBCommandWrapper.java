/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.command.IServerContext;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.tinydb.TinyDBServerState;
import com.github.tonivade.tinydb.TinyDBSessionState;
import com.github.tonivade.tinydb.TransactionState;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.command.annotation.PubSubAllowed;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.command.annotation.TxIgnore;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.IDatabase;

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
    public void execute(IRequest request, IResponse response) {
        IDatabase db = getCurrentDB(request);
        if (request.getLength() < params) {
            response.addError("ERR wrong number of arguments for '" + request.getCommand() + "' command");
        } else if (dataType != null && !db.isType(safeKey(request.getParam(0)), dataType)) {
            response.addError("WRONGTYPE Operation against a key holding the wrong kind of value");
        } else if (isSubscribed(request) && !pubSubAllowed) {
            response.addError("ERR only (P)SUBSCRIBE / (P)UNSUBSCRIBE / QUIT allowed in this context");
        } else if (isTxActive(request) && !txIgnore) {
            enqueueRequest(request);
            response.addSimpleStr("QUEUED");
        } else {
            if (command instanceof ITinyDBCommand) {
                executeTinyDBCommand(db, request, response);
            } else if (command instanceof ICommand) {
                executeCommand(request, response);
            }
        }
    }

    private void executeCommand(IRequest request, IResponse response) {
        ((ICommand) command).execute(request, response);
    }

    private void executeTinyDBCommand(IDatabase db, IRequest request, IResponse response) {
        ((ITinyDBCommand) command).execute(db, request, response);
    }

    private void enqueueRequest(IRequest request) {
        getTransactionState(request.getSession()).enqueue(request);
    }

    private boolean isTxActive(IRequest request) {
        return getTransactionState(request.getSession()) != null;
    }

    private TransactionState getTransactionState(ISession session) {
        return session.getValue("tx");
    }

    private IDatabase getCurrentDB(IRequest request) {
        TinyDBServerState serverState = getServerState(request.getServerContext());
        TinyDBSessionState sessionState = getSessionState(request.getSession());
        return serverState.getDatabase(sessionState.getCurrentDB());
    }

    private TinyDBSessionState getSessionState(ISession session) {
        return session.getValue("state");
    }

    private TinyDBServerState getServerState(IServerContext server) {
        return server.getValue("state");
    }

    private boolean isSubscribed(IRequest request) {
        return getSessionState(request.getSession()).isSubscribed();
    }

}
