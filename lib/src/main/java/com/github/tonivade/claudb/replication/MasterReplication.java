/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.replication;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.DBServerState;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisTokenVisitor;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public class MasterReplication implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(MasterReplication.class);

  private static final String SELECT_COMMAND = "SELECT";
  private static final String PING_COMMAND = "PING";
  private static final int TASK_DELAY = 2;

  private final DBServerContext server;
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public MasterReplication(DBServerContext server) {
    this.server = requireNonNull(server);
  }

  public void start() {
    executor.scheduleWithFixedDelay(this, TASK_DELAY, TASK_DELAY, TimeUnit.SECONDS);
  }

  public void stop() {
    executor.shutdown();
  }

  public void addSlave(String id) {
    getServerState().addSlave(id);
    LOGGER.info("new slave: {}", id);
  }

  public void removeSlave(String id) {
    getServerState().removeSlave(id);
    LOGGER.info("slave revomed: {}", id);
  }

  @Override
  public void run() {
    List<RedisToken> commands = createCommands();

    for (SafeString slave : getServerState().getSlaves()) {
      for (RedisToken command : commands) {
        server.publish(slave.toString(), command);
      }
    }
  }

  private List<RedisToken> createCommands() {
    List<RedisToken> commands = new LinkedList<>();
    commands.add(pingCommand());
    commands.addAll(commandsToReplicate());
    return commands;
  }

  private List<RedisToken> commandsToReplicate() {
    List<RedisToken> commands = new LinkedList<>();

    for (RedisToken command : server.getCommandsToReplicate()) {
      command.accept(new AbstractRedisTokenVisitor<Void>() {
        @Override
        public Void array(ArrayRedisToken token) {
          commands.add(selectCommand(token));
          commands.add(command(token));
          return null;
        }
      });
    }
    return commands;
  }

  private RedisToken selectCommand(ArrayRedisToken token) {
    return array(string(SELECT_COMMAND),
        token.getValue().stream().findFirst().orElse(string("0")));
  }

  private RedisToken pingCommand() {
    return array(string(PING_COMMAND));
  }

  private RedisToken command(ArrayRedisToken token) {
    return array(token.getValue().stream().skip(1).collect(toList()));
  }

  private DBServerState getServerState() {
    return serverState().getOrElseThrow(() -> new IllegalStateException("missing server state"));
  }

  private Option<DBServerState> serverState() {
    return server.getValue("state");
  }
}
