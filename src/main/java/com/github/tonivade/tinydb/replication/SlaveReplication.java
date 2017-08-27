/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.replication;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.RedisToken.visit;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.entry;
import static com.github.tonivade.tinydb.data.DatabaseValue.hash;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.resp.RespCallback;
import com.github.tonivade.resp.RespClient;
import com.github.tonivade.resp.command.DefaultRequest;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.StringRedisToken;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenVisitor;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerContext;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.persistence.ByteBufferInputStream;

public class SlaveReplication implements RespCallback {

  private static final DatabaseKey MASTER_KEY = safeKey("master");

  private static final Logger LOGGER = LoggerFactory.getLogger(SlaveReplication.class);

  private static final String SYNC_COMMAND = "SYNC";

  private final RespClient client;
  private final TinyDBServerContext server;
  private final Session session;
  private final String host;
  private final int port;

  public SlaveReplication(TinyDBServerContext server, Session session, String host, int port) {
    this.server = server;
    this.session = session;
    this.host = host;
    this.port = port;
    this.client = new RespClient(host, port, this);
  }

  public void start() {
    client.start();
    server.setMaster(false);
    server.getAdminDatabase().put(MASTER_KEY, createState(false));
  }

  public void stop() {
    client.stop();
    server.setMaster(true);
  }

  @Override
  public void onConnect() {
    LOGGER.info("Connected with master");
    client.send(array(string(SYNC_COMMAND)));
    server.getAdminDatabase().put(MASTER_KEY, createState(true));
  }

  @Override
  public void onDisconnect() {
    LOGGER.info("Disconnected from master");
    server.getAdminDatabase().put(MASTER_KEY, createState(false));
  }

  @Override
  public void onMessage(RedisToken token) {
    switch (token.getType()) {
    case STRING:
      processRDB((StringRedisToken) token);
      break;
    case ARRAY:
      processCommand((ArrayRedisToken) token);
      break;

    default:
      break;
    }
  }

  private void processCommand(ArrayRedisToken token) {
    Collection<RedisToken> array = token.getValue();
    StringRedisToken commandToken = (StringRedisToken) array.stream().findFirst().orElse(nullString());
    List<RedisToken> paramTokens =array.stream().skip(1).collect(toList());

    LOGGER.debug("command recieved from master: {}", commandToken);

    RespCommand command = server.getCommand(commandToken.getValue().toString());

    if (command != null) {
      command.execute(request(commandToken, paramTokens));
    }
  }

  private Request request(StringRedisToken commandToken, List<RedisToken> array) {
    return new DefaultRequest(server, session, commandToken.getValue(), arrayToList(array));
  }

  private List<SafeString> arrayToList(List<RedisToken> request) {
    RedisTokenVisitor<SafeString> visitor = 
        RedisTokenVisitor.<SafeString>builder().onString(StringRedisToken::getValue).build();
    return visit(request.stream(), visitor).collect(toList());
  }

  private void processRDB(StringRedisToken token) {
    try {
      SafeString value = token.getValue();
      server.importRDB(toStream(value));
      LOGGER.info("loaded RDB file from master");
    } catch (IOException e) {
      LOGGER.error("error importing RDB file", e);
    }
  }

  private InputStream toStream(SafeString value) {
    return new ByteBufferInputStream(value.getBytes());
  }

  private DatabaseValue createState(boolean connected) {
    return hash(entry(safeString("host"), safeString(host)),
                entry(safeString("port"), safeString(valueOf(port))),
                entry(safeString("state"), safeString(connected ? "connected" : "disconnected")));
  }
}
