/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import com.github.tonivade.resp.command.CommandWrapper;
import com.github.tonivade.resp.command.CommandWrapperFactory;
import com.github.tonivade.resp.command.RespCommand;

public class DBCommandWrapperFactory implements CommandWrapperFactory {
  @Override
  public RespCommand wrap(RespCommand command) {
    if (command instanceof DBCommand) {
      return new DBCommandWrapper((DBCommand) command);
    }
    return new CommandWrapper(command);
  }
}
