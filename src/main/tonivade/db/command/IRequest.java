/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import java.util.List;
import java.util.Optional;

import tonivade.db.redis.SafeString;

public interface IRequest {

    /**
     * @return the command
     */
    public String getCommand();

    /**
     * @return the params
     */
    public List<String> getParams();

    public List<SafeString> getSafeParams();

    public String getParam(int i);

    public SafeString getSafeParam(int i);

    public Optional<String> getOptionalParam(int i);

    public int getLength();

    public ISession getSession();

    public IServerContext getServerContext();

}