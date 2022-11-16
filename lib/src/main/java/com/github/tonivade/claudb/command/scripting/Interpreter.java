/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.List;

public interface Interpreter {

  RedisToken execute(SafeString script, List<SafeString> keys, List<SafeString> params);

  static Interpreter nullEngine() {
    return (script, keys, params) -> error("interpreter disabled");
  }
}
