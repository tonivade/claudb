/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.status;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import com.github.tonivade.resp.RespCallback;
import com.github.tonivade.resp.RespClient;
import com.github.tonivade.resp.RespServer;
import com.github.tonivade.resp.protocol.RedisToken;

@com.github.tonivade.claudb.junit5.ClauDBTest
public class DBClientTest {
  
  static RespServer server = ClauDB.builder().randomPort().build();

  @Test
  public void testClient() {
    ArgumentCaptor<RedisToken> captor = ArgumentCaptor.forClass(RedisToken.class);

    RespCallback callback = mock(RespCallback.class);
    RespClient client = new RespClient(DBServerContext.DEFAULT_HOST, server.getPort(), callback);

    client.start();

    verify(callback, timeout(2000)).onConnect();

    client.send(array(string("ping")));

    verify(callback, timeout(5000)).onMessage(captor.capture());

    assertThat(captor.getValue(), equalTo(status("PONG")));

    client.stop();

    verify(callback, timeout(5000)).onDisconnect();
  }

}
