/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.persistence;

import static com.github.tonivade.resp.protocol.RedisToken.string;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.TinyDBConfig;
import com.github.tonivade.tinydb.TinyDBServerContext;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.util.HexUtil;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceManagerTest {

  private static final String DEFAULT_CHARSET = "UTF-8";
  private static final String REDO_FILE = "redo.aof";
  private static final String DUMP_FILE = "dump.rdb";

  @Mock
  private TinyDBServerContext server;

  private PersistenceManager manager;

  @Before
  public void setUp()  {
    this.manager = new PersistenceManager(server, TinyDBConfig.builder().withPersistence().build());
    deleteFiles();
  }

  @After
  public void tearDown()  {
    deleteFiles();
  }

  @Test
  public void testRun() throws IOException {
    doAnswer(new ExportRDB()).when(server).exportRDB(any());

    manager.run();

    RDBInputStream input = new RDBInputStream(new FileInputStream(DUMP_FILE));

    Map<Integer, Map<DatabaseKey, DatabaseValue>> databases = input.parse();

    assertThat(databases, notNullValue());
  }

  @Test
  public void testStop() throws IOException {
    manager.stop();

    verify(server).exportRDB(any());

    assertThat(new File(DUMP_FILE).exists(), is(true));
  }

  @Test
  public void testStart() throws IOException {
    RespCommand cmd = mock(RespCommand.class);
    when(server.getCommand(anyString())).thenReturn(cmd);

    writeRDB();
    writeAOF();

    manager.start();

    verify(server).importRDB(any());
    verify(cmd).execute(any());

    assertThat(new File(REDO_FILE).exists(), is(true));
  }

  @Test
  public void testAppend() throws InterruptedException {
    manager.start();
    manager.append(pingCommand());

    Thread.sleep(1000);

    assertThat(readAOF(), is("*1\r\n$4\r\nPING\r\n"));
  }

  private void deleteFiles() {
    deleteFile(DUMP_FILE);
    deleteFile(REDO_FILE);
  }

  private void deleteFile(String name) {
    File file = new File(name);
    if (file.exists()) {
      if (file.delete()) {
        System.out.println(file + " deleted");
      }
    }
  }

  private void writeRDB() {
    try (FileOutputStream out = new FileOutputStream(DUMP_FILE)) {
      out.write("Test".getBytes(DEFAULT_CHARSET));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeAOF() {
    try (FileOutputStream out = new FileOutputStream(REDO_FILE)) {
      out.write("*1\r\n$4\r\nPING\r\n".getBytes(DEFAULT_CHARSET));
    } catch (IOException e) {
      e.printStackTrace();
    }
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

  private RedisToken pingCommand() {
    return RedisToken.array(string("PING"));
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
