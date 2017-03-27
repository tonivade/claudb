/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public class LuaInterpreterTest {

  private LuaInterpreter interpreter = new LuaInterpreter();

  @Test
  public void keys() {
    RedisToken token = interpreter.execute(safeString("return KEYS[1]"),
                                           asList(safeString("key1")),
                                           emptyList());

    assertThat(token, equalTo(string("key1")));
  }

  @Test
  public void argv() {
    RedisToken token = interpreter.execute(safeString("return ARGV[1]"),
                                           asList(safeString("key1")),
                                           asList(safeString("value1")));

    assertThat(token, equalTo(string("value1")));
  }

  @Test
  public void keysAndArgv() {
    RedisToken token = interpreter.execute(safeString("return {KEYS[1], ARGV[1]}"),
                                           asList(safeString("key1")),
                                           asList(safeString("value1")));

    assertThat(token, equalTo(array(string("key1"), string("value1"))));
  }

  @Test
  public void number() {
    RedisToken token = interpreter.execute(safeString("return 1"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(RedisToken.integer(1)));
  }

  @Test
  public void boolTrue() {
    RedisToken token = interpreter.execute(safeString("return true"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(RedisToken.integer(1)));
  }

  @Test
  public void boolFalse() {
    RedisToken token = interpreter.execute(safeString("return false"),
                                           emptyList(),
                                           emptyList());

    assertThat(token, equalTo(RedisToken.string(SafeString.EMPTY_STRING)));
  }

}
