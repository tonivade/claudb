/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.event;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tonivade.tinydb.TinyDBServerContext;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

@RunWith(MockitoJUnitRunner.class)
public class NotificationManagerTest {
  
  @Mock
  private TinyDBServerContext server;
  @Mock
  private Database database;

  @InjectMocks
  private NotificationManager manager;
  
  @Test
  public void enqueue() {
    String client = "client:7070";
    String pattern = "__key*__:*";
    KeyEvent event = new KeyEvent(safeString("set"), safeString("key"), 0);

    when(server.getAdminDatabase()).thenReturn(database);
    when(database.entrySet())
      .thenReturn(asSet(entry(safeKey("psubscriptions:" + pattern), set(safeString(client)))));
    
    manager.enqueue(event);
    
    verify(server, timeout(1000)).publish(client, 
        array(string("PMESSAGE"), string(pattern), string(event.getChannel()), string("set")));
  }

  private Set<Entry<DatabaseKey, DatabaseValue>> asSet(SimpleEntry<DatabaseKey, DatabaseValue> entry) {
    return Stream.of(entry).collect(toSet());
  }

  private SimpleEntry<DatabaseKey, DatabaseValue> entry(DatabaseKey key, DatabaseValue value) {
    return new AbstractMap.SimpleEntry<DatabaseKey, DatabaseValue>(key, value);
  }
}
