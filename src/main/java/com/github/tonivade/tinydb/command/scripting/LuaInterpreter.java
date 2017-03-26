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
  public Object execute(SafeString script, List<SafeString> keys, List<SafeString> params) {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("luaj");
      engine.put("KEYS", toArray(keys));
      engine.put("ARGV", toArray(params));
      return engine.eval(script.toString());
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    return RedisToken.status(IResponse.RESULT_OK);
  }

  private Object[] toArray(List<SafeString> keys) {
    return keys.stream().map(SafeString::toString).toArray(String[]::new);
  }

}
