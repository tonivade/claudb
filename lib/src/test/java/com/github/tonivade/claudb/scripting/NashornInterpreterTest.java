/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.status;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import com.github.tonivade.resp.protocol.RedisToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NashornInterpreterTest {

  @Mock
  private RedisLibrary redis;

  private NashornInterpreter interpreter;

  @Before
  public void setUp() {
    interpreter = new NashornInterpreter(new NashornRedisBinding(redis));
  }

  @Test
  public void keys() {
    RedisToken token = interpreter.execute(safeString("KEYS[0]"),
                                           asList(safeString("key1")),
                                           emptyList());

    assertThat(token, equalTo(string("key1")));
  }

  @Test
  public void argv() {
    RedisToken token = interpreter.execute(safeString("ARGV[0]"),
                                           asList(safeString("key1")),
                                           asList(safeString("value1")));

    assertThat(token, equalTo(string("value1")));
  }

  @Test
  public void keysAndArgv() {
    RedisToken token = interpreter.execute(safeString("[KEYS[0], ARGV[0]]"),
                                           asList(safeString("key1")),
                                           asList(safeString("value1")));

    assertThat(token, equalTo(array(string("key1"), string("value1"))));
  }

  @Test
  public void number() {
    RedisToken token = interpreter.execute(safeString("1"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(integer(1)));
  }

  @Test
  public void boolTrue() {
    RedisToken token = interpreter.execute(safeString("true"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(integer(1)));
  }

  @Test
  public void boolFalse() {
    RedisToken token = interpreter.execute(safeString("false"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(nullString()));
  }

  @Test
  public void ping() {
    when(redis.call(safeString("ping"))).thenReturn(status("PONG"));

    RedisToken token = interpreter.execute(safeString("redis.call(\"ping\")"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(status("PONG")));
  }

  @Test
  public void echo() {
    when(redis.call(safeString("echo"), safeString("hello"))).thenReturn(string("hello"));

    RedisToken token = interpreter.execute(safeString("redis.call(\"echo\", \"hello\")"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(string("hello")));
  }

  @Test
  public void pcall() {
    when(redis.pcall(safeString("echo"), safeString("hello"))).thenReturn(error("message"));

    RedisToken token = interpreter.execute(safeString("redis.pcall(\"echo\", \"hello\")"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(error("message")));
  }
}
