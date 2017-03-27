/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;

public class LuaInterpreterTest {

  private LuaInterpreter interpreter = new LuaInterpreter();

  @Test
  public void keys() {
    Object token = interpreter.execute(safeString("return KEYS[1]"),
                                       asList(safeString("key1")),
                                       emptyList());

    assertThat(token, equalTo("key1"));
  }

  @Test
  public void argv() {
    Object token = interpreter.execute(safeString("return ARGV[1]"),
                                       asList(safeString("key1")),
                                       asList(safeString("value1")));

    assertThat(token, equalTo("value1"));
  }

  @Test
  public void keysAndArgv() {
    LuaTable token = (LuaTable) interpreter.execute(safeString("return {KEYS[1], ARGV[1]}"),
                                       asList(safeString("key1")),
                                       asList(safeString("value1")));

    assertThat(token.get(1), equalTo(LuaString.valueOf("key1")));
    assertThat(token.get(2), equalTo(LuaString.valueOf("value1")));
  }

}
