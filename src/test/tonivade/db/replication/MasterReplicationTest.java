/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.redis.SafeString.safeString;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IServerContext;
import tonivade.db.data.Database;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.db.redis.RedisArray;
import tonivade.db.redis.RedisToken.StringRedisToken;

@RunWith(MockitoJUnitRunner.class)
public class MasterReplicationTest {

    @Mock
    private IServerContext server;

    @InjectMocks
    private MasterReplication master;

    private IDatabase db = new Database(new HashMap<String, DatabaseValue>());

    @Test
    public void testReplication() throws Exception {
        when(server.getCommands()).thenReturn(asList(request()));
        when(server.getAdminDatabase()).thenReturn(db);

        master.addSlave("slave:1");
        master.addSlave("slave:2");

        master.start();

        verify(server, timeout(10000).times(2)).publish(anyString(), anyString());
    }

    private RedisArray request() {
        RedisArray array = new RedisArray();
        array.add(new StringRedisToken(safeString("set")));
        array.add(new StringRedisToken(safeString("a")));
        array.add(new StringRedisToken(safeString("b")));
        return array;
    }

}
