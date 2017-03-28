package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.status;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.luaj.vm2.LuaValue.valueOf;
import static org.luaj.vm2.LuaValue.varargsOf;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.luaj.vm2.Varargs;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tonivade.resp.command.IResponse;

@RunWith(MockitoJUnitRunner.class)
public class RedisBindingTest {
  @Mock
  private RedisLibrary redis;
  @InjectMocks
  private RedisBinding binding;

  @Test
  public void call() {
    when(redis.call(safeString("command"), safeString("param1"), safeString("param2")))
      .thenReturn(status(IResponse.RESULT_OK));
    
    Varargs result = binding.invoke(varargsOf(valueOf("command"), valueOf("param1"), valueOf("param2")));
    
    assertThat(result.optjstring(1, "null"), equalTo(IResponse.RESULT_OK));
  }
}
