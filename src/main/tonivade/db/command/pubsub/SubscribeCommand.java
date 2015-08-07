/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static java.util.Arrays.asList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.set;
import static tonivade.db.redis.SafeString.safeString;

import java.util.HashSet;
import java.util.Set;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.PubSubAllowed;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;
import tonivade.db.redis.SafeString;

@ReadOnly
@Command("subscribe")
@ParamLength(1)
@PubSubAllowed
public class SubscribeCommand implements ICommand {

    private static final SafeString SUBSCRIBE = safeString("subscribe");

    private static final String SUBSCRIPTIONS_PREFIX = "subscriptions:";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        IDatabase admin = request.getServerContext().getAdminDatabase();
        int i = 1;
        for (SafeString channel : request.getParams()) {
            admin.merge(safeKey(SUBSCRIPTIONS_PREFIX + channel), set(request.getSession().getId()),
                    (oldValue, newValue) -> {
                        Set<String> merge = new HashSet<>();
                        merge.addAll(oldValue.getValue());
                        merge.add(request.getSession().getId());
                        return set(merge);
                    });
            request.getSession().addSubscription(channel);
            response.addArray(asList(SUBSCRIBE, channel, i++));
        }
    }

}
