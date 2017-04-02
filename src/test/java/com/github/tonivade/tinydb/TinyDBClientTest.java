/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.github.tonivade.resp.IRedisCallback;
import com.github.tonivade.resp.RedisClient;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;
import com.github.tonivade.tinydb.ITinyDB;

public class TinyDBClientTest {

    @Rule
    public final TinyDBRule rule = new TinyDBRule();

    @Test
    public void testClient()  {
        ArgumentCaptor<RedisToken> captor = ArgumentCaptor.forClass(RedisToken.class);

        IRedisCallback callback = mock(IRedisCallback.class);
        RedisClient client = new RedisClient(ITinyDB.DEFAULT_HOST, ITinyDB.DEFAULT_PORT, callback);

        client.start();

        verify(callback, timeout(1000)).onConnect();

        client.send("ping");

        verify(callback, timeout(1000)).onMessage(captor.capture());

        RedisToken message = captor.getValue();

        assertThat(message.getType(), is(RedisTokenType.STATUS));
        assertThat(message.getValue(), is(safeString("PONG")));

        client.stop();

        verify(callback, timeout(1000)).onDisconnect();
    }

}
