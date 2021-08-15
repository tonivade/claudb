/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.claudb.DatabaseValueMatchers.entry;
import static com.github.tonivade.claudb.DatabaseValueMatchers.list;
import static com.github.tonivade.claudb.DatabaseValueMatchers.score;
import static com.github.tonivade.claudb.DatabaseValueMatchers.set;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(TypeCommand.class)
public class TypeCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecuteString() {
    rule.withData("a", string("string"))
    .withParams("a").execute()
    .assertThat(RedisToken.status("string"));
  }

  @Test
  public void testExecuteHash() {
    rule.withData("a", hash(entry("k1", "v1")))
    .withParams("a")
    .execute()
    .assertThat(RedisToken.status("hash"));
  }

  @Test
  public void testExecuteList() {
    rule.withData("a", list("a", "b", "c"))
    .withParams("a")
    .execute()
    .assertThat(RedisToken.status("list"));
  }

  @Test
  public void testExecuteSet() {
    rule.withData("a", set("a", "b", "c"))
    .withParams("a")
    .execute()
    .assertThat(RedisToken.status("set"));
  }

  @Test
  public void testExecuteZSet() {
    rule.withData("a", zset(score(1.0, "a"), score(2.0, "b"), score(3.0, "c")))
    .withParams("a")
    .execute()
    .assertThat(RedisToken.status("zset"));
  }

  @Test
  public void testExecuteNotExists() {
    rule.withData("a", string("string"))
    .withParams("b").execute()
    .assertThat(RedisToken.status("none"));
  }

}
