/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import java.util.List;
import java.util.Optional;

import tonivade.db.redis.SafeString;

public interface IRequest {

    public String getCommand();

    public List<SafeString> getParams();

    public SafeString getParam(int i);

    public Optional<SafeString> getOptionalParam(int i);

    public int getLength();

    public ISession getSession();

    public IServerContext getServerContext();

}