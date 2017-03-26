package com.github.tonivade.tinydb.command.scripting;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public class LuaInterpreter implements Interpreter {

  @Override
  public RedisToken execute(SafeString script, List<SafeString> keys, List<SafeString> params) {
    try {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByName("luaj");
      engine.put("KEYS", toArray(keys));
      engine.put("ARGV", toArray(params));
      Object result = engine.eval(script.toString());
      System.out.println(result);
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    return RedisToken.status(IResponse.RESULT_OK);
  }

  private Object[] toArray(List<SafeString> keys) {
    return keys.stream().map(SafeString::toString).toArray(String[]::new);
  }

}
