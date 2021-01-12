/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.zset;

import static com.github.tonivade.claudb.data.DatabaseValue.score;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(SortedSetIncrementByCommand.class)
public class SortedSetIncrementByCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void notExists() {
    rule.withParams("zset", "10.0", "value1")
        .execute()
        .assertValue("zset", equalTo(zset(score(10.0, safeString("value1")))))
        .assertThat(string("10.0"));
  }

  @Test
  public void exists() {
    rule.withData("zset", zset(score(1.0, safeString("value1"))))
        .withParams("zset", "10", "value1")
        .execute()
        .assertValue("zset", equalTo(zset(score(11.0, safeString("value1")))))
        .assertThat(string("11.0"));
  }

  @Test
  public void invalidValue() throws Exception {
    rule.withParams("zset", "asdf", "value1")
        .execute()
        .assertThat(error("ERR value is not an integer or out of range"));
  }
}
