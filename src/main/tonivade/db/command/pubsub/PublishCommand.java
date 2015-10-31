/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static java.util.Arrays.asList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.server.protocol.SafeString.safeString;

import java.util.Set;

import tonivade.db.command.IRedisCommand;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;
import tonivade.server.command.Response;
import tonivade.server.protocol.SafeString;

@Command("publish")
@ParamLength(2)
public class PublishCommand implements IRedisCommand {

    private static final SafeString MESSAGE = safeString("message");

    private static final String SUBSCRIPTIONS_PREFIX = "subscriptions:";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        IDatabase admin = getAdminDatabase(request.getServerContext());
        DatabaseValue value = getSubscriptors(admin, request.getParam(0));

        Set<SafeString> subscribers = value.<Set<SafeString>>getValue();
        for (SafeString subscriber : subscribers) {
            publish(request, subscriber);
        }

        response.addInt(subscribers.size());
    }

    private void publish(IRequest request, SafeString subscriber) {
        getTinyDB(request.getServerContext()).publish(subscriber.toString(), message(request));
    }

    private DatabaseValue getSubscriptors(IDatabase admin, SafeString channel) {
        DatabaseKey subscriptorsKey = safeKey(safeString(SUBSCRIPTIONS_PREFIX + channel));
        return admin.getOrDefault(subscriptorsKey, DatabaseValue.EMPTY_SET);
    }

    private String message(IRequest request) {
        Response stream = new Response();
        stream.addArray(asList(MESSAGE, request.getParam(0), request.getParam(1)));
        return stream.toString();
    }

}
