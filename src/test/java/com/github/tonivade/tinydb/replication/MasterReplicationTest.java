/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.replication;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisToken.ArrayRedisToken;
import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.TinyDBServerState;

@RunWith(MockitoJUnitRunner.class)
public class MasterReplicationTest {

    @Mock
    private ITinyDB server;

    @InjectMocks
    private MasterReplication master;

    private final TinyDBServerState serverState = new TinyDBServerState(1);

    @Test
    public void testReplication()  {
        when(server.getCommandsToReplicate()).thenReturn(asList(request()));
        when(server.getValue("state")).thenReturn(Optional.of(serverState));

        master.addSlave("slave:1");
        master.addSlave("slave:2");

        master.start();

        verify(server, timeout(3000).times(3)).publish(eq("slave:1"), any(RedisToken.class));
        verify(server, timeout(3000).times(3)).publish(eq("slave:2"), any(RedisToken.class));
    }

    private ArrayRedisToken request() {
        return array(integer(0), string("set"), string("a"), string("b"));
    }

}
