/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import io.netty.channel.ChannelHandlerContext;

import java.util.Set;

public interface ISession {

    public String getId();

    public ChannelHandlerContext getContext();

    public Set<String> getSubscriptions();

    public void addSubscription(String channel);

    public void removeSubscription(String channel);

    public int getCurrentDB();

    public void setCurrentDB(int db);

}
