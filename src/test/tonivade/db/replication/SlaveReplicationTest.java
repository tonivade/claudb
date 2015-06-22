/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.io.InputStream;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
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
    public void testReplication() throws Exception {
        SlaveReplication slave = new SlaveReplication(context, "localhost", 7081);

        slave.start();

        verify(context, timeout(2000)).importRDB(captor.capture());

        InputStream stream = captor.getValue();

        byte[] buffer = new byte[stream.available()];

        int readed = stream.read(buffer);

        if (readed != buffer.length) {
            fail("read fail");
        }

        // XXX: not working
        System.out.println(HexUtil.toHexString(buffer));

        Assert.assertThat(buffer.length, CoreMatchers.is(18));
    }

}
