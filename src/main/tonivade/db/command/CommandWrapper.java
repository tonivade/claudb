/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.command.annotation.PubSubAllowed;
import tonivade.db.data.DataType;
import tonivade.db.data.IDatabase;

public class CommandWrapper implements ICommand {

    private int params;

    private DataType dataType;

    private final boolean pubSubAllowed;

    private final ICommand command;

    public CommandWrapper(ICommand command) {
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
    public void execute(IDatabase db, IRequest request, IResponse response) {
        if (request.getLength() < params) {
            response.addError("ERR wrong number of arguments for '" + request.getCommand() + "' command");
        } else if (dataType != null && !db.isType(request.getParam(0), dataType)) {
            response.addError("WRONGTYPE Operation against a key holding the wrong kind of value");
        } else if (isSubscribed(request) && !pubSubAllowed) {
            response.addError("ERR only (P)SUBSCRIBE / (P)UNSUBSCRIBE / QUIT allowed in this context");
        } else {
            command.execute(db, request, response);
        }
    }

    private boolean isSubscribed(IRequest request) {
        return !request.getSession().getSubscriptions().isEmpty();
    }

}
