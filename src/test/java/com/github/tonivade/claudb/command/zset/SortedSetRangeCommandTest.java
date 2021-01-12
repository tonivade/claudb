/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.zset;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.claudb.DatabaseValueMatchers.score;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(SortedSetRangeCommand.class)
public class SortedSetRangeCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "0", "-1")
    .execute()
    .assertThat(array(string("a"), string("b"), string("c")));
  }

  @Test
  public void testExecuteWithScores()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "0", "-1", "WITHSCORES")
    .execute()
    .assertThat(array(string("a"), string("1.0"), 
        string("b"), string("2.0"), 
        string("c"), string("3.0")));
  }

  @Test
  public void testExecuteHead()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "0", "1")
    .execute()
    .assertThat(array(string("a"), string("b")));
  }

  @Test
  public void testExecuteTail()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "-2", "-1")
    .execute()
    .assertThat(array(string("b"), string("c")));
  }

  @Test
  public void testExecuteToOutOfRange()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "1", "4")
    .execute()
    .assertThat(array(string("b"), string("c")));
  }

  @Test
  public void testExecuteFromOutOfRange()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "4", "6")
    .execute()
    .assertThat(RedisToken.array());
  }

  @Test
  public void testExecuteFromOrder()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "-1", "0")
    .execute()
    .assertThat(RedisToken.array());
  }

  @Test
  public void testExecuteOne()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "0", "0")
    .execute()
    .assertThat(array(string("a")));
  }

  @Test
  public void testExecuteNoExists()  {
    rule.withParams("key", "0", "-1")
    .execute()
    .assertThat(RedisToken.array());
  }

}
