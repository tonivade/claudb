/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static tonivade.db.redis.SafeString.safeAsList;

import java.util.Set;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.Response;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("publish")
@ParamLength(2)
public class PublishCommand implements ICommand {

    private static final String SUBSCRIPTIONS_PREFIX = "subscriptions:";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        IDatabase admin = request.getServerContext().getAdminDatabase();
        DatabaseValue value = admin.getOrDefault(SUBSCRIPTIONS_PREFIX + request.getParam(0), DatabaseValue.EMPTY_SET);

        Set<String> subscribers = value.<Set<String>>getValue();
        for (String subscriber : subscribers) {
            request.getServerContext().publish(subscriber, message(request));
        }

        response.addInt(subscribers.size());
    }

    private String message(IRequest request) {
        Response stream = new Response();
        stream.addArray(safeAsList("message", request.getParam(0), request.getParam(1)));
        return stream.toString();
    }

}
