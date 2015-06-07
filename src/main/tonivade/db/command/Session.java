/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashSet;
import java.util.Set;

public class Session implements ISession {

    private String id;

    private ChannelHandlerContext ctx;

    private int db;

    private Set<String> subscriptions = new HashSet<>();

    public Session(String id, ChannelHandlerContext ctx) {
        super();
        this.id = id;
        this.ctx = ctx;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ChannelHandlerContext getContext() {
        return ctx;
    }

    @Override
    public int getCurrentDB() {
        return db;
    }

    @Override
    public void setCurrentDB(int db) {
        this.db = db;
    }

    @Override
    public Set<String> getSubscriptions() {
        return subscriptions;
    }

    @Override
    public void addSubscription(String channel) {
        subscriptions.add(channel);
    }

    @Override
    public void removeSubscription(String channel) {
        subscriptions.remove(channel);
    }

}
