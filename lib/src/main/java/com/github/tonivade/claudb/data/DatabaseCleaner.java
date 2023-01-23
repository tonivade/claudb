/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.resp.util.Precondition.checkNonNull;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.claudb.DBConfig;
import com.github.tonivade.claudb.DBServerContext;

public class DatabaseCleaner {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseCleaner.class);

  private final DBServerContext server;
  private final DBConfig config;

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public DatabaseCleaner(DBServerContext server, DBConfig config) {
    this.server = checkNonNull(server);
    this.config = checkNonNull(config);
  }

  public void start() {
    executor.scheduleWithFixedDelay(this::clean,
        config.getCleanPeriod(), config.getCleanPeriod(), TimeUnit.SECONDS);
  }

  public void stop() {
    executor.shutdown();
  }

  private void clean() {
    LOGGER.debug("cleaning database: running");
    server.clean(Instant.now());
    LOGGER.debug("cleaning database: done");
  }
}
