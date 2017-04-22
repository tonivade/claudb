/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.replication;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.tonivade.resp.RespCallback;
import com.github.tonivade.resp.RespClient;
import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.RedisToken.StringRedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.persistence.ByteBufferInputStream;

public class SlaveReplication implements RespCallback {

  private static final Logger LOGGER = Logger.getLogger(SlaveReplication.class.getName());

  private static final String SYNC_COMMAND = "SYNC";

  private final RespClient client;
  private final ITinyDB server;
  private final ISession session;

  public SlaveReplication(ITinyDB server, ISession session, String host, int port) {
    this.server = server;
    this.session = session;
    this.client = new RespClient(host, port, this);
  }

  public void start() {
    client.start();
    server.setMaster(false);
  }

  public void stop() {
    client.stop();
    server.setMaster(true);
  }

  @Override
  public void onConnect() {
    LOGGER.info(() -> "Connected with master");
    client.send(array(string(SYNC_COMMAND)));
  }

  @Override
  public void onDisconnect() {
    LOGGER.info(() -> "Disconnected from master");
  }

  @Override
  public void onMessage(RedisToken<?> token) {
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
    Collection<RedisToken<?>> array = token.getValue();
    RedisToken<SafeString> commandToken = (RedisToken<SafeString>) array.stream().findFirst().orElse(nullString());
    List<RedisToken<SafeString>> paramTokens = List.class.cast(array.stream().skip(1).collect(toList()));

    LOGGER.fine(() -> "command recieved from master: " + commandToken.getValue());

    ICommand command = server.getCommand(commandToken.getValue().toString());

    if (command != null) {
      command.execute(request(commandToken, paramTokens));
    }
  }

  private Request request(RedisToken<SafeString> commandToken, List<RedisToken<SafeString>> array) {
    return new Request(server, session, commandToken.getValue(), arrayToList(array));
  }

  private List<SafeString> arrayToList(List<RedisToken<SafeString>> request) {
    return request.stream().skip(1).map(RedisToken::getValue).collect(toList());
  }

  private void processRDB(StringRedisToken token) {
    try {
      SafeString value = token.getValue();
      server.importRDB(toStream(value));
      LOGGER.info(() -> "loaded RDB file from master");
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "error importing RDB file", e);
    }
  }

  private InputStream toStream(SafeString value) throws UnsupportedEncodingException {
    return new ByteBufferInputStream(value.getBytes());
  }

}
