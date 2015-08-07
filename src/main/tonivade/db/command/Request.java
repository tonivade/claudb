/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import tonivade.db.redis.SafeString;

public class Request implements IRequest {

    private SafeString command;

    private List<SafeString> params;

    private ISession session;

    private IServerContext server;

    public Request(IServerContext server, ISession session, SafeString command, List<SafeString> params) {
        super();
        this.server = server;
        this.session = session;
        this.command = command;
        this.params = params;
    }

    @Override
    public String getCommand() {
        return command.toString();
    }

    @Override
    public List<SafeString> getParams() {
        return Collections.unmodifiableList(params);
    }

    @Override
    public SafeString getParam(int i) {
        if (i < params.size()) {
            return params.get(i);
        }
        return null;
    }

    @Override
    public Optional<SafeString> getOptionalParam(int i) {
        return Optional.ofNullable(getParam(i));
    }

    @Override
    public int getLength() {
        return params.size();
    }

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public IServerContext getServerContext() {
        return server;
    }

    @Override
    public String toString() {
        return command + "[" + params.size() + "]: " + params;
    }

}
