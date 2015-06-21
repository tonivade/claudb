/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.TinyDB;
import tonivade.db.command.IServerContext;
import tonivade.db.persistence.HexUtil;

@RunWith(MockitoJUnitRunner.class)
public class SlaveReplicationTest {

    private static TinyDB server;

    static {
        server = new TinyDB();
        server.start();
    }

    @Mock
    private IServerContext context;

    @Captor
    private ArgumentCaptor<InputStream> captor;

    @Test
    public void testName() throws Exception {
        SlaveReplication slave = new SlaveReplication(context, "localhost", 7081);

        slave.start();

        verify(context, timeout(2000)).importRDB(captor.capture());

        InputStream stream = captor.getValue();

        byte[] buffer = new byte[stream.available()];

        stream.read(buffer);

        // XXX: not working
        System.out.println(HexUtil.toHexString(buffer));

        //assertThat(stream.available(), is(37));
    }

}
