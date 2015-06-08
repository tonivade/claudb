/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static tonivade.db.data.DatabaseValue.set;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.IDatabase;

@Command("unsubscribe")
@ParamLength(1)
public class UnsubscribeCommand implements ICommand {

    private static final String SUBSCRIPTIONS_PREFIX = "subscriptions:";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        IDatabase admin = request.getServerContext().getDatabase();
        int i = request.getLength();
        for (String channel : request.getParams()) {
            admin.merge(SUBSCRIPTIONS_PREFIX + channel, set(request.getSession().getId()),
                    (oldValue, newValue) -> {
                        Set<String> merge = new HashSet<>();
                        merge.addAll(oldValue.getValue());
                        merge.remove(request.getSession().getId());
                        return set(merge);
                    });
            request.getSession().removeSubscription(channel);
            response.addArray(Arrays.asList("unsubscribe", channel, String.valueOf(--i)));
        }
    }

}
