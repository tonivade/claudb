/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import tonivade.db.redis.RedisToken;

public interface ITinyDBCallback {

    public void onConnect();

    public void onDisconnect();

    public void onMessage(RedisToken token);

}
