/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static tonivade.db.redis.SafeString.safeString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import tonivade.db.command.IServerContext;
import tonivade.db.data.IDatabase;
import tonivade.db.redis.RedisArray;
import tonivade.db.redis.RedisToken;
import tonivade.db.redis.RedisToken.StringRedisToken;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceManagerTest {

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String REDO_FILE = "redo.aof";
    private static final String DUMP_FILE = "dump.rdb";

    @Mock
    private IServerContext server;

    @InjectMocks
    private PersistenceManager manager;

    @Before
    public void setUp() throws Exception {
        deleteFiles();
    }

    @After
    public void tearDown() throws Exception {
        deleteFiles();
    }

    private void deleteFiles() {
        new File(DUMP_FILE).delete();
        new File(REDO_FILE).delete();
    }

    @Test
    public void testRun() throws Exception {
        doAnswer(new ExportRDB()).when(server).exportRDB(any());

        manager.run();

        RDBInputStream input = new RDBInputStream(new FileInputStream(DUMP_FILE));

        Map<Integer, IDatabase> databases = input.parse();

        assertThat(databases, notNullValue());
    }

    @Test
    public void testStart() throws Exception {
        writeRDB();

        manager.start();

        verify(server).importRDB(any());

        assertThat(new File(REDO_FILE).exists(), is(true));
    }

    @Test
    public void testAppend() throws Exception {
        manager.start();
        manager.append(array());

        Thread.sleep(1000);

        assertThat(readAOF(), is("*1\r\n$4\r\nPING\r\n"));
    }

    private String readAOF() {
        String str = null;
        try (FileInputStream in = new FileInputStream(REDO_FILE)) {
            byte[] buffer = new byte[1024];
            int readed = in.read(buffer);
            if (readed > -1) {
                str = new String(buffer, 0, readed, DEFAULT_CHARSET);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    private RedisArray array() {
        RedisArray array = new RedisArray();
        array.add(token("PING"));
        return array;
    }

    private RedisToken token(String string) {
        return new StringRedisToken(safeString(string));
    }

    private void writeRDB() {
        try (FileOutputStream out = new FileOutputStream(DUMP_FILE)) {
            out.write("Test".getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStop() throws Exception {
        manager.stop();

        verify(server).exportRDB(any());

        assertThat(new File(DUMP_FILE).exists(), is(true));
    }

    private static class ExportRDB implements Answer<Void> {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            OutputStream output = (OutputStream) invocation.getArguments()[0];
            output.write(HexUtil.toByteArray("524544495330303033FE00FF77DE0394AC9D23EA"));
            return null;
        }
    }

}
