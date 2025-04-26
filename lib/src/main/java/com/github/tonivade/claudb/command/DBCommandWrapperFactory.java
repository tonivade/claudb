/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import com.github.tonivade.resp.command.CommandWrapperFactory;
import com.github.tonivade.resp.command.RespCommand;

public class DBCommandWrapperFactory implements CommandWrapperFactory {
  @Override
  public RespCommand wrap(Object command) {
    return new DBCommandWrapper(command);
  }
}
