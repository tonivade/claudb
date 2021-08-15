/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.RedisTokenType.UNKNOWN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisParser;
import com.github.tonivade.resp.protocol.RedisSource;
import com.github.tonivade.resp.protocol.RedisToken;

public class RedisSourceInputStreamTest {

  private String input = "*4\r\n" +
      "$1\r\n" +
      "0\r\n" +
      "$3\r\n" +
      "set\r\n" +
      "$1\r\n" +
      "a\r\n" +
      "$1\r\n" +
      "1\r\n" +
      "*4\r\n" +
      "$1\r\n" +
      "0\r\n" +
      "$3\r\n" +
      "set\r\n" +
      "$1\r\n" +
      "b\r\n" +
      "$1\r\n" +
      "2\r\n" +
      "*4\r\n" +
      "$1\r\n" +
      "0\r\n" +
      "$3\r\n" +
      "set\r\n" +
      "$1\r\n" +
      "c\r\n" +
      "$1\r\n" +
      "3\r\n";

  @Test
  public void parse() {
    RedisParser parser = createParser(input);

    List<RedisToken> tokens = readTokens(parser);

    assertThat(tokens, contains(array(string("0"), string("set"), string("a"), string("1")),
                                array(string("0"), string("set"), string("b"), string("2")),
                                array(string("0"), string("set"), string("c"), string("3"))));
  }

  private static RedisParser createParser(String input) {
    InputStream stream = new ByteArrayInputStream(input.getBytes(UTF_8));
    RedisSource source = new RedisSourceInputStream(stream);
    return new RedisParser(1024 * 1024, source);
  }

  private List<RedisToken> readTokens(RedisParser parser) {
    List<RedisToken> tokens = new LinkedList<>();
    while (true) {
      RedisToken token = parser.next();
      if (token.getType() == UNKNOWN) {
        break;
      }
      tokens.add(token);
    }
    return tokens;
  }
}
