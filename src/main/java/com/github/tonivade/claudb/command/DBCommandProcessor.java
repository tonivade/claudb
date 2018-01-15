/*
 * Copyright (c) 2015-2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.visit;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.resp.command.DefaultRequest;
import com.github.tonivade.resp.command.DefaultSession;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.StringRedisToken;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenVisitor;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.DBServerContext;

public class DBCommandProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(DBCommandProcessor.class);

  private final DBServerContext server;
  private final Session session;
  
  public DBCommandProcessor(DBServerContext server) {
    this(server,  new DefaultSession("dummy", null));
  }
  
  public DBCommandProcessor(DBServerContext server, Session session) {
    this.server = server;
    this.session = session;
  }

  public void processCommand(ArrayRedisToken token) {
    Collection<RedisToken> array = token.getValue();
    StringRedisToken commandToken = (StringRedisToken) array.stream().findFirst().orElse(nullString());
    List<RedisToken> paramTokens = array.stream().skip(1).collect(toList());

    LOGGER.debug("new command recieved: {}", commandToken);

    RespCommand command = server.getCommand(commandToken.getValue().toString());

    if (command != null) {
      command.execute(request(commandToken, paramTokens));
    }
  }

  private Request request(StringRedisToken commandToken, List<RedisToken> array) {
    return new DefaultRequest(server, session, commandToken.getValue(), arrayToList(array));
  }

  private List<SafeString> arrayToList(List<RedisToken> request) {
    RedisTokenVisitor<SafeString> visitor = RedisTokenVisitor.<SafeString>builder()
        .onString(StringRedisToken::getValue).build();
    return visit(request.stream(), visitor).collect(toList());
  }

}
