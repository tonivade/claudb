/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.tonivade.resp.protocol.RedisToken;

import tonivade.db.ITinyDB;
import tonivade.db.TinyDBServerState;

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

        verify(server, timeout(3000).times(3)).publish(eq("slave:1"), any(RedisToken.class));
        verify(server, timeout(3000).times(3)).publish(eq("slave:2"), any(RedisToken.class));
    }

    private List<RedisToken> request() {
        return asList(integer(0), string("set"), string("a"), string("b"));
    }

}
