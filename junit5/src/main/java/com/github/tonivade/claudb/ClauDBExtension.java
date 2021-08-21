/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import java.util.function.IntSupplier;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.github.tonivade.resp.RespServer;

public class ClauDBExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {
  
  private final RespServer server;
  
  public ClauDBExtension() {
    this.server = ClauDB.builder().randomPort().build();
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
    return serverPort();
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Class<?> type = parameterContext.getParameter().getType();
    return IntSupplier.class.equals(type);
  }

  private IntSupplier serverPort() {
    return server::getPort;
  }
}
