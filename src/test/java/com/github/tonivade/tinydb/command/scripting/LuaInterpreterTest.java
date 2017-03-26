package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;

public class LuaInterpreterTest {

  private LuaInterpreter interpreter = new LuaInterpreter();

  @Test
  public void testExecute() throws Exception {
    RedisToken token = interpreter.execute(safeString("return KEYS[1]"), asList(safeString("key1")), emptyList());

    System.out.println(token);
  }

}
