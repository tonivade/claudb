/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static com.github.tonivade.purefun.Precondition.checkNonNull;

import org.junit.rules.ExternalResource;

import com.github.tonivade.resp.RespServer;

public class ClauDBRule extends ExternalResource {

  private final RespServer server;

  protected ClauDBRule(RespServer server) {
    this.server = checkNonNull(server);
  }
  
  public String getHost() {
    return server.getHost();
  }
  
  public int getPort() {
    return server.getPort();
  }
  
  @Override
  protected void before() throws Throwable {
    server.start();
  }
  
  @Override
  protected void after() {
    server.stop();
  }
  
  public static ClauDBRule defaultPort() {
    return new ClauDBRule(ClauDB.builder().build());
  }
  
  public static ClauDBRule port(int port) {
    return new ClauDBRule(ClauDB.builder().port(port).build());
  }
  
  public static ClauDBRule randomPort() {
    return new ClauDBRule(ClauDB.builder().randomPort().build());
  }
}
