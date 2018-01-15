/*
 * Copyright (c) 2015-2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.tonivade.resp.RespServer;

public class ClauDBExtension implements BeforeEachCallback, AfterEachCallback {
  
  private final RespServer server;
  
  public ClauDBExtension() {
    this.server = ClauDB.builder().build();
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    server.start();
  }
  
  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    server.stop();
  }
}
