/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.github.tonivade.resp.RespServer;

public class ClauDBRule implements TestRule {

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
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          server.start();
          base.evaluate();
        } finally {
          server.stop();
        }
      }
    };
  }
}
