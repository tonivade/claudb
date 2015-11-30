/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static java.util.Arrays.asList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.set;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.Collection;
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
@Command("unsubscribe")
@ParamLength(1)
@PubSubAllowed
public class UnsubscribeCommand implements ITinyDBCommand {

    private static final SafeString UNSUBSCRIBE = safeString("unsubscribe");
    private static final String SUBSCRIPTIONS_PREFIX = "subscriptions:";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        IDatabase admin = getAdminDatabase(request.getServerContext());
        Collection<SafeString> channels = getChannels(request);
        int i = channels.size();
        for (SafeString channel : channels) {
            admin.merge(safeKey(safeString(SUBSCRIPTIONS_PREFIX + channel)), set(safeString(request.getSession().getId())),
                    (oldValue, newValue) -> {
                        Set<SafeString> merge = new HashSet<>();
                        merge.addAll(oldValue.getValue());
                        merge.remove(safeString(request.getSession().getId()));
                        return set(merge);
                    });
            getSessionState(request.getSession()).removeSubscription(channel);
            response.addArray(asList(UNSUBSCRIBE, channel, --i));
        }
    }

    private Collection<SafeString> getChannels(IRequest request) {
        if (request.getParams().isEmpty()) {
            return getSessionState(request.getSession()).getSubscriptions();
        }
        return request.getParams();
    }

}
