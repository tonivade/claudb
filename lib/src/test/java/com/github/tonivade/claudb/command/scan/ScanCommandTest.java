/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scan;

import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.StringRedisToken;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;

@CommandUnderTest(ScanCommand.class)
public class ScanCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void withoutPattern() {
    RedisToken response = rule
      .withData("a", string("a"))
      .withData("b", string("b"))
      .withData("s", set(safeString("1"), safeString("2"), safeString("3")))
      .withParams("0")
      .execute()
      .getResponse();

    assertThat(response.getType(), equalTo(RedisTokenType.ARRAY));

    ArrayRedisToken array = (ArrayRedisToken) response;

    Iterator<RedisToken> iterator = array.getValue().iterator();
    StringRedisToken cursor = (StringRedisToken) iterator.next();
    ArrayRedisToken result = (ArrayRedisToken) iterator.next();

    assertThat(cursor.getValue(), equalTo(safeString("3")));
    assertThat(result.getValue(), containsInAnyOrder(RedisToken.string("a"), RedisToken.string("b"), RedisToken.string("s")));
  }

  @Test
  public void withPattern() {
    RedisToken response = rule
      .withData("a", string("a"))
      .withData("ab", string("b"))
      .withData("s", set(safeString("1"), safeString("2"), safeString("3")))
      .withParams("0", "match", "a*")
      .execute()
      .getResponse();

    assertThat(response.getType(), equalTo(RedisTokenType.ARRAY));

    ArrayRedisToken array = (ArrayRedisToken) response;

    Iterator<RedisToken> iterator = array.getValue().iterator();
    StringRedisToken cursor = (StringRedisToken) iterator.next();
    ArrayRedisToken result = (ArrayRedisToken) iterator.next();

    assertThat(cursor.getValue(), equalTo(safeString("2")));
    assertThat(result.getValue(), containsInAnyOrder(RedisToken.string("a"), RedisToken.string("ab")));
  }

  @Test
  public void withCount() {
    RedisToken response = rule
      .withData("a", string("a"))
      .withData("ab", string("b"))
      .withData("s", set(safeString("1"), safeString("2"), safeString("3")))
      .withParams("0", "count", "2")
      .execute()
      .getResponse();

    assertThat(response.getType(), equalTo(RedisTokenType.ARRAY));

    ArrayRedisToken array = (ArrayRedisToken) response;

    Iterator<RedisToken> iterator = array.getValue().iterator();
    StringRedisToken cursor = (StringRedisToken) iterator.next();
    ArrayRedisToken result = (ArrayRedisToken) iterator.next();

    assertThat(cursor.getValue(), equalTo(safeString("2")));
    assertThat(result.getValue(), containsInAnyOrder(RedisToken.string("a"), RedisToken.string("ab")));
  }

  @Test
  public void empty() {
    rule
      .withParams("0")
      .execute()
      .assertThat(array(RedisToken.string("0"), array()));
  }

}
