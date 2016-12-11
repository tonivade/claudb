/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static tonivade.db.data.DatabaseKey.safeKey;

import java.util.Set;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

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

    private RedisToken message(IRequest request) {
        return array(string(MESSAGE), string(request.getParam(0)), string(request.getParam(1)));
    }

}
