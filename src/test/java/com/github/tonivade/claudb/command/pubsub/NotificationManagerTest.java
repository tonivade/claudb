/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.event.Event;
import com.github.tonivade.claudb.event.NotificationManager;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;

@RunWith(MockitoJUnitRunner.class)
public class NotificationManagerTest {
  
  @Mock
  private DBServerContext server;
  @Mock
  private Database database;

  @InjectMocks
  private NotificationManager manager;
  
  @Test
  public void enqueue() {
    String client = "client:7070";
    String pattern = "__key*__@*";
    Event event = Event.keyEvent(safeString("set"), safeString("key"), 0);

    when(server.getAdminDatabase()).thenReturn(database);
    when(database.entrySet())
      .thenReturn(asSet(entry(safeKey("psubscription:" + pattern), set(safeString(client)))));
    
    manager.enqueue(event);
    
    verify(server, timeout(1000)).publish(client, 
        array(string("pmessage"), string(pattern), string(event.getChannel()), string("set")));
  }

  private Set<Tuple2<DatabaseKey, DatabaseValue>> asSet(Tuple2<DatabaseKey, DatabaseValue> entry) {
    return Stream.of(entry).toSet();
  }

  private Tuple2<DatabaseKey, DatabaseValue> entry(DatabaseKey key, DatabaseValue value) {
    return Tuple.of(key, value);
  }
}
