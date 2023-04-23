/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scan;

import static com.github.tonivade.claudb.data.DatabaseValue.score;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.StringRedisToken;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;

@CommandUnderTest(SortedSetScanCommand.class)
public class SortedSetScanCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void withoutPattern() {
    RedisToken response = rule
      .withData("z", DatabaseValue.zset(
        score(1, safeString("a")),
        score(2, safeString("ab")),
        score(3, safeString("c"))))
      .withParams("z", "0")
      .execute()
      .getResponse();

    assertThat(response.getType(), equalTo(RedisTokenType.ARRAY));

    ArrayRedisToken array = (ArrayRedisToken) response;

    Iterator<RedisToken> iterator = array.getValue().iterator();
    StringRedisToken cursor = (StringRedisToken) iterator.next();
    ArrayRedisToken result = (ArrayRedisToken) iterator.next();

    assertThat(cursor.getValue(), equalTo(safeString("3")));
    assertThat(result.getValue(), containsInAnyOrder(
      string("1.0"), string("a"),
      string("2.0"), string("ab"),
      string("3.0"), string("c")));
  }

  @Test
  public void withPattern() {
    RedisToken response = rule
      .withData("z", DatabaseValue.zset(
        score(1, safeString("a")),
        score(2, safeString("ab")),
        score(3, safeString("c"))))
      .withParams("z", "0", "match", "a*")
      .execute()
      .getResponse();

    assertThat(response.getType(), equalTo(RedisTokenType.ARRAY));

    ArrayRedisToken array = (ArrayRedisToken) response;

    Iterator<RedisToken> iterator = array.getValue().iterator();
    StringRedisToken cursor = (StringRedisToken) iterator.next();
    ArrayRedisToken result = (ArrayRedisToken) iterator.next();

    assertThat(cursor.getValue(), equalTo(safeString("2")));
    assertThat(result.getValue(), containsInAnyOrder(
      string("1.0"), string("a"),
      string("2.0"), string("ab")));
  }

  @Test
  public void withCount() {
    RedisToken response = rule
      .withData("z", DatabaseValue.zset(
        score(1, safeString("a")),
        score(2, safeString("ab")),
        score(3, safeString("c"))))
      .withParams("z", "0", "count", "2")
      .execute()
      .getResponse();

    assertThat(response.getType(), equalTo(RedisTokenType.ARRAY));

    ArrayRedisToken array = (ArrayRedisToken) response;

    Iterator<RedisToken> iterator = array.getValue().iterator();
    StringRedisToken cursor = (StringRedisToken) iterator.next();
    ArrayRedisToken result = (ArrayRedisToken) iterator.next();

    assertThat(cursor.getValue(), equalTo(safeString("2")));
    assertThat(result.getValue(), containsInAnyOrder(
      string("1.0"), string("a"),
      string("2.0"), string("ab")));
  }

  @Test
  public void empty() {
    rule
      .withData("z", DatabaseValue.EMPTY_ZSET)
      .withParams("z", "0")
      .execute()
      .assertThat(array(string("0"), array()));
  }

  @Test
  public void notExists() {
    rule
      .withParams("z", "0")
      .execute()
      .assertThat(array(string("0"), array()));
  }

}
