package com.github.tonivade.tinydb.command.scripting;

import java.util.List;

import com.github.tonivade.resp.protocol.SafeString;

public interface Interpreter {
  Object execute(SafeString script, List<SafeString> keys, List<SafeString> params);
}
