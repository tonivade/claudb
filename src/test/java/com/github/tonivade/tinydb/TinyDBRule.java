/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb;

import static com.github.tonivade.tinydb.TinyDBConfig.withoutPersistence;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TinyDBRule  implements TestRule {

  private final TinyDB server;

  public TinyDBRule() {
    this(ITinyDB.DEFAULT_HOST, ITinyDB.DEFAULT_PORT);
  }

  public TinyDBRule(String host, int port) {
    this(host, port, withoutPersistence());
  }

  public TinyDBRule(String host, int port, TinyDBConfig config) {
    this.server = new TinyDB(host, port, config);
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
