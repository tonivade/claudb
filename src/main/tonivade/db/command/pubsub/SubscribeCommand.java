/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static java.util.Arrays.asList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.set;
import static tonivade.server.protocol.SafeString.safeString;

import java.util.HashSet;
import java.util.Set;

import tonivade.db.command.IRedisCommand;
import tonivade.db.command.annotation.PubSubAllowed;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;
import tonivade.server.protocol.SafeString;

@ReadOnly
@Command("subscribe")
@ParamLength(1)
@PubSubAllowed
public class SubscribeCommand implements IRedisCommand {

    private static final SafeString SUBSCRIBE = safeString("subscribe");

    private static final String SUBSCRIPTIONS_PREFIX = "subscriptions:";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        IDatabase admin = getAdminDatabase(request.getServerContext());
        int i = 1;
        for (SafeString channel : request.getParams()) {
            admin.merge(safeKey(safeString(SUBSCRIPTIONS_PREFIX + channel)), set(safeString(request.getSession().getId())),
                    (oldValue, newValue) -> {
                        Set<SafeString> merge = new HashSet<>();
                        merge.addAll(oldValue.getValue());
                        merge.add(safeString(request.getSession().getId()));
                        return set(merge);
                    });
            getSessionState(request.getSession()).addSubscription(channel);
            response.addArray(asList(SUBSCRIBE, channel, i++));
        }
    }

}
