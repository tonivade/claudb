package com.github.tonivade.tinydb.data;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.tinydb.TinyDBConfig;
import com.github.tonivade.tinydb.TinyDBServerContext;

public class DatabaseCleaner {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseCleaner.class);

  private final TinyDBServerContext server;
  private final TinyDBConfig config;

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public DatabaseCleaner(TinyDBServerContext server, TinyDBConfig config) {
    this.server = server;
    this.config = config;
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
