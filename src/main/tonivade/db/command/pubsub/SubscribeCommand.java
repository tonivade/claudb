/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static java.util.Arrays.asList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.set;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.HashSet;
import java.util.Set;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.PubSubAllowed;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.protocol.SafeString;

@ReadOnly
@Command("subscribe")
@ParamLength(1)
@PubSubAllowed
public class SubscribeCommand implements ITinyDBCommand {

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
