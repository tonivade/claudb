/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import static tonivade.db.data.DatabaseKey.safeKey;

import tonivade.db.RedisServerState;
import tonivade.db.RedisSessionState;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.command.annotation.PubSubAllowed;
import tonivade.db.data.DataType;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.ICommand;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;

public class RedisCommandWrapper implements ICommand {

    private int params;

    private DataType dataType;

    private final boolean pubSubAllowed;

    private final IRedisCommand command;

    public RedisCommandWrapper(IRedisCommand command) {
        this.command = command;
        ParamLength length = command.getClass().getAnnotation(ParamLength.class);
        if (length != null) {
            this.params = length.value();
        }
        ParamType type = command.getClass().getAnnotation(ParamType.class);
        if (type != null) {
            this.dataType = type.value();
        }
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
        } else {
            command.execute(db, request, response);
        }
    }

    private IDatabase getCurrentDB(IRequest request) {
        RedisServerState serverState = getServerState(request);
        RedisSessionState sessionState = getSessionState(request);
        return serverState.getDatabase(sessionState.getCurrentDB());
    }

    private RedisSessionState getSessionState(IRequest request) {
        return request.getSession().getValue("state");
    }

    private RedisServerState getServerState(IRequest request) {
        return request.getServerContext().getValue("state");
    }

    private boolean isSubscribed(IRequest request) {
        return !getSessionState(request).getSubscriptions().isEmpty();
    }

}
