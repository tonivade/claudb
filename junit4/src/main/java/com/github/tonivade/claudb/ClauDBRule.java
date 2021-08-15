/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import org.junit.rules.ExternalResource;

import com.github.tonivade.resp.RespServer;

public class ClauDBRule extends ExternalResource {

  private final RespServer server;

  public ClauDBRule() {
    this(DBServerContext.DEFAULT_HOST, DBServerContext.DEFAULT_PORT);
  }

  public ClauDBRule(String host, int port) {
    this(host, port, DBConfig.builder().withoutPersistence().build());
  }

  public ClauDBRule(String host, int port, DBConfig config) {
    this.server = ClauDB.builder().host(host).port(port).config(config).build();
  }
  
  @Override
  protected void before() throws Throwable {
    server.start();
  }
  
  @Override
  protected void after() {
    server.stop();
  }
}
