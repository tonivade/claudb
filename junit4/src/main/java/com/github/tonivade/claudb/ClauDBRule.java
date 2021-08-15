/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import org.junit.rules.ExternalResource;

import com.github.tonivade.resp.RespServer;

public class ClauDBRule extends ExternalResource {

  private final ClauDB claudb;
  private final RespServer server;

  public ClauDBRule() {
    this(DBServerContext.DEFAULT_HOST, DBServerContext.DEFAULT_PORT);
  }

  public ClauDBRule(String host, int port) {
    this(host, port, DBConfig.builder().withoutPersistence().build());
  }

  public ClauDBRule(String host, int port, DBConfig config) {
    this.claudb = new ClauDB(host, port, config);
    this.server = new RespServer(claudb);
  }
  
  public String getHost() {
    return claudb.getHost();
  }
  
  public int getPort() {
    return claudb.getPort();
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
