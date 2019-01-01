/*
 * Copyright (c) 2016-2019, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
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
