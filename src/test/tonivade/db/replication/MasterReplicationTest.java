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

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IServerContext;
import tonivade.db.command.Request;
import tonivade.db.data.Database;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

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
        when(server.getDatabase()).thenReturn(db);

        master.addSlave("slave:1");
        master.addSlave("slave:2");

        master.start();

        verify(server, timeout(10000).times(2)).publish(anyString(), anyString());
    }

    private IRequest request() {
        return new Request(server, null, "set", asList("a", "b"));
    }

}
