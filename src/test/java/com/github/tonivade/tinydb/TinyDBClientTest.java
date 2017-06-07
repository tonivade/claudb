/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.status;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.github.tonivade.resp.RespCallback;
import com.github.tonivade.resp.RespClient;
import com.github.tonivade.resp.protocol.RedisToken;

public class TinyDBClientTest {

  @Rule
  public final TinyDBRule rule = new TinyDBRule();

  @Test
  public void testClient()  {
    ArgumentCaptor<RedisToken> captor = ArgumentCaptor.forClass(RedisToken.class);

    RespCallback callback = mock(RespCallback.class);
    RespClient client = new RespClient(TinyDBServerContext.DEFAULT_HOST, TinyDBServerContext.DEFAULT_PORT, callback);

    client.start();

    verify(callback, timeout(1000)).onConnect();

    client.send(array(string("ping")));

    verify(callback, timeout(1000)).onMessage(captor.capture());

    assertThat(captor.getValue(), equalTo(status("PONG")));

    client.stop();

    verify(callback, timeout(1000)).onDisconnect();
  }

}
