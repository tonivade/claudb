/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.tonivade.resp.RespServer;

public class ClauDBExtension implements BeforeAllCallback, AfterAllCallback {
  
  private final RespServer server;
  
  public ClauDBExtension() {
    this.server = ClauDB.builder().build();
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    server.start();
  }
  
  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    server.stop();
  }
}
