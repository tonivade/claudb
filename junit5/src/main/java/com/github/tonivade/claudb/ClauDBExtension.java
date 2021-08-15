/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.github.tonivade.resp.RespServer;

public class ClauDBExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {
  
  private final ClauDB claudb;
  private final RespServer server;
  
  public ClauDBExtension() {
    this.claudb = new ClauDB();
    this.server = new RespServer(claudb);
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    server.start();
  }
  
  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    server.stop();
  }
  
  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return claudb;
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Class<?> type = parameterContext.getParameter().getType();
    return ClauDB.class.equals(type);
  }
}
