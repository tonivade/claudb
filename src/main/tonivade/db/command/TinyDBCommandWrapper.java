/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import static tonivade.db.data.DatabaseKey.safeKey;
import tonivade.db.TinyDBServerState;
import tonivade.db.TinyDBSessionState;
import tonivade.db.TransactionState;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.command.annotation.PubSubAllowed;
import tonivade.db.command.annotation.TxIgnore;
import tonivade.db.data.DataType;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.ICommand;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.command.IServerContext;
import tonivade.redis.command.ISession;

public class TinyDBCommandWrapper implements ICommand {

    private int params;

    private DataType dataType;

    private final boolean pubSubAllowed;

    private final boolean txIgnore;

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
        this.txIgnore = command.getClass().isAnnotationPresent(TxIgnore.class);
        this.pubSubAllowed = command.getClass().isAnnotationPresent(PubSubAllowed.class);
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
