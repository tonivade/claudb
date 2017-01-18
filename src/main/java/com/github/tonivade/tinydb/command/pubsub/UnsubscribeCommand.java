/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.ITinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.PubSubAllowed;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.IDatabase;

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
