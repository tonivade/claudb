/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.ITinyDB;
import tonivade.db.TinyDBServerState;
import tonivade.redis.protocol.RedisToken;
import tonivade.redis.protocol.RedisToken.ArrayRedisToken;
import tonivade.redis.protocol.RedisToken.IntegerRedisToken;
import tonivade.redis.protocol.RedisToken.StringRedisToken;

@RunWith(MockitoJUnitRunner.class)
public class MasterReplicationTest {

    @Mock
    private ITinyDB server;

    @InjectMocks
    private MasterReplication master;

    private final TinyDBServerState serverState = new TinyDBServerState(1);

    @Test
    public void testReplication() throws Exception {
        when(server.getCommandsToReplicate()).thenReturn(asList(request()));
        when(server.getValue("state")).thenReturn(serverState);

        master.addSlave("slave:1");
        master.addSlave("slave:2");

        master.start();

        verify(server, timeout(3000).times(3)).publish(eq("slave:1"), any(ArrayRedisToken.class));
        verify(server, timeout(3000).times(3)).publish(eq("slave:2"), any(ArrayRedisToken.class));
    }

    private List<RedisToken> request() {
        List<RedisToken> array = new ArrayList<>();
        array.add(new IntegerRedisToken(0));
        array.add(new StringRedisToken(safeString("set")));
        array.add(new StringRedisToken(safeString("a")));
        array.add(new StringRedisToken(safeString("b")));
        return array;
    }

}
