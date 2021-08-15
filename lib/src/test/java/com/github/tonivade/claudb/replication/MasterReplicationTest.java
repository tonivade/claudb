/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.replication;

import static com.github.tonivade.purefun.data.Sequence.listOf;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.DBServerState;
import com.github.tonivade.claudb.data.OnHeapDatabaseFactory;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.protocol.RedisToken;

@RunWith(MockitoJUnitRunner.class)
public class MasterReplicationTest {

  @Mock
  private DBServerContext server;

  @InjectMocks
  private MasterReplication master;

  private final DBServerState serverState = new DBServerState(new OnHeapDatabaseFactory(), 1);

  @Test
  public void testReplication()  {
    when(server.getCommandsToReplicate()).thenReturn(listOf(request()));
    when(server.getValue("state")).thenReturn(Option.some(serverState));

    master.addSlave("slave:1");
    master.addSlave("slave:2");

    master.start();

    verify(server, timeout(3000).times(3)).publish(eq("slave:1"), any(RedisToken.class));
    verify(server, timeout(3000).times(3)).publish(eq("slave:2"), any(RedisToken.class));
  }

  private RedisToken request() {
    return array(integer(0), string("set"), string("a"), string("b"));
  }
}
