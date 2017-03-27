/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import java.util.List;

import com.github.tonivade.resp.protocol.SafeString;

public interface Interpreter {
  Object execute(SafeString script, List<SafeString> keys, List<SafeString> params);
}
