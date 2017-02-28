/*
 * Copyright (c) 2016-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command;

import com.github.tonivade.resp.command.CommandWrapperFactory;
import com.github.tonivade.resp.command.ICommand;

public class TinyDBCommandWrapperFactory implements CommandWrapperFactory {
  @Override
  public ICommand wrap(Object command) {
    return new TinyDBCommandWrapper(command);
  }
}
