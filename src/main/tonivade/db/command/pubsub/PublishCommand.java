/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static java.util.Arrays.asList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.Set;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.command.Response;
import tonivade.redis.protocol.SafeString;

@Command("publish")
@ParamLength(2)
public class PublishCommand implements ITinyDBCommand {

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
