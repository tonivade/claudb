/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.luaj.vm2.LuaValue.valueOf;
import static org.luaj.vm2.LuaValue.varargsOf;
import static org.mockito.Mockito.when;
import com.github.tonivade.resp.command.ServerContext;
import com.github.tonivade.resp.command.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LuaRedisBindingTest {

  @Mock
  private ServerContext context;
  @Mock
  private Session session;

  @InjectMocks
  private RedisLibrary redis;

  @Test
  public void call() {
    when(context.getCommand("command")).thenReturn(request -> responseOk());

    Varargs result = new LuaRedisBinding(redis).call().invoke(varargsOf(valueOf("command"), valueOf("param1"), valueOf("param2")));

    assertThat(result.checktable(1).get("ok"), equalTo(LuaValue.valueOf("OK")));
  }

  @Test
  public void callOnError() {
    when(context.getCommand("command")).thenThrow(new UnsupportedOperationException("message"));

    VarArgFunction call = new LuaRedisBinding(redis).call();
    Varargs args = varargsOf(valueOf("command"), valueOf("param1"), valueOf("param2"));

    assertThrows(UnsupportedOperationException.class, () -> call.invoke(args));
  }

  @Test
  public void pcall() {
    when(context.getCommand("command")).thenReturn(request -> responseOk());

    Varargs result = new LuaRedisBinding(redis).pcall().invoke(varargsOf(valueOf("command"), valueOf("param1"), valueOf("param2")));

    assertThat(result.checktable(1).get("ok"), equalTo(LuaValue.valueOf("OK")));
  }

  @Test
  public void pcallOnError() {
    when(context.getCommand("command")).thenThrow(new UnsupportedOperationException("message"));

    Varargs result = new LuaRedisBinding(redis).pcall().invoke(varargsOf(valueOf("command"), valueOf("param1"), valueOf("param2")));

    assertThat(result.checktable(1).get("error"), equalTo(LuaValue.valueOf("message")));
  }
}
