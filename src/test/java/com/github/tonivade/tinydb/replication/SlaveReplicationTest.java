/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.replication;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.tinydb.persistence.HexUtil.toHexString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.TinyDBRule;

@RunWith(MockitoJUnitRunner.class)
public class SlaveReplicationTest {

    @Rule
    public final TinyDBRule rule = new TinyDBRule();

    @Mock
    private ITinyDB context;

    @Mock
    private ISession session;

    @Mock
    private ICommand command;

    @Captor
    private ArgumentCaptor<IRequest> requestCaptor;

    @Captor
    private ArgumentCaptor<InputStream> captor;

    @Test
    public void testReplication() throws Exception {
        SlaveReplication slave = new SlaveReplication(context, session, "localhost", 7081);

        slave.start();

        verifyConectionAndRDBDumpImported();
    }

    @Test
    public void testProcessCommand() throws Exception {
        when(context.getCommand("PING")).thenReturn(command);

        SlaveReplication slave = new SlaveReplication(context, session, "localhost", 7081);

        slave.onMessage(array(string("PING")));

        verifyCommandExecuted();
    }

    private void verifyCommandExecuted() {
        verify(command).execute(requestCaptor.capture(), any(IResponse.class));

        IRequest request = requestCaptor.getValue();
        assertThat(request.getCommand(), is("PING"));
    }

    private void verifyConectionAndRDBDumpImported() throws IOException {
        verify(context, timeout(2000)).importRDB(captor.capture());

        InputStream stream = captor.getValue();

        byte[] buffer = new byte[stream.available()];

        int readed = stream.read(buffer);

        assertThat(readed, is(buffer.length));
        assertThat(toHexString(buffer), is("524544495330303036FF224AF218835A1E69"));
    }

}
