/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import io.netty.channel.ChannelHandlerContext;

import java.util.Set;

import tonivade.db.redis.SafeString;

public interface ISession {

    public String getId();

    public ChannelHandlerContext getContext();

    public Set<SafeString> getSubscriptions();

    public void addSubscription(SafeString channel);

    public void removeSubscription(SafeString channel);

    public int getCurrentDB();

    public void setCurrentDB(int db);

    public void enqueue(Runnable task);

    public void destroy();

}
