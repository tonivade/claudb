/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import tonivade.server.ITinyCallback;
import tonivade.server.TinyClient;
import tonivade.server.protocol.RedisToken;
import tonivade.server.protocol.RedisTokenType;

public class TinyDBClientTest {

    @Rule
    public final TinyDBRule rule = new TinyDBRule();

    @Test
    public void testClient() throws Exception {
        ArgumentCaptor<RedisToken> captor = ArgumentCaptor.forClass(RedisToken.class);

        ITinyCallback callback = mock(ITinyCallback.class);
        TinyClient client = new TinyClient(ITinyDB.DEFAULT_HOST, ITinyDB.DEFAULT_PORT, callback);

        client.start();

        verify(callback, timeout(1000)).onConnect();

        client.send("ping\r\n");

        verify(callback, timeout(1000)).onMessage(captor.capture());

        RedisToken message = captor.getValue();

        assertThat(message.getType(), is(RedisTokenType.STATUS));
        assertThat(message.getValue(), is("PONG"));

        client.stop();

        verify(callback, timeout(1000)).onDisconnect();
    }

}
