/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static tonivade.db.persistence.HexUtil.toHexString;

import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.TinyDBRule;
import tonivade.db.command.IServerContext;
import tonivade.db.command.ISession;

@RunWith(MockitoJUnitRunner.class)
public class SlaveReplicationTest {

    @Rule
    public final TinyDBRule rule = new TinyDBRule();

    @Mock
    private IServerContext context;

    @Mock
    private ISession session;

    @Captor
    private ArgumentCaptor<InputStream> captor;

    @Test
    public void testReplication() throws Exception {
        SlaveReplication slave = new SlaveReplication(context, session, "localhost", 7081);

        slave.start();

        verify(context, timeout(2000)).importRDB(captor.capture());

        InputStream stream = captor.getValue();

        byte[] buffer = new byte[stream.available()];

        int readed = stream.read(buffer);

        assertThat(readed, is(buffer.length));
        assertThat(toHexString(buffer), is("524544495330303036FF224AF218835A1E69"));
    }

}
