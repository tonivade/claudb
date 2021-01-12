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

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(SortedSetRangeByScoreCommand.class)
public class SortedSetRangeByScoreCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "1", "3")
    .execute()
    .assertThat(array(string("a"), string("b"), string("c")));
  }

  @Test
  public void testExecuteWithScores()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "1", "3", "WITHSCORES")
    .execute()
    .assertThat(array(string("a"), string("1.0"), 
        string("b"), string("2.0"), 
        string("c"), string("3.0")));
  }

  @Test
  public void testExecuteWithLimit()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "1", "3", "LIMIT", "1", "2")
    .execute()
    .assertThat(array(string("b"), string("c")));
  }

  @Test
  public void testExecuteExclusive()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "(1", "3")
    .execute()
    .assertThat(array(string("b"), string("c")));
  }

  @Test
  public void testExecuteInfinity()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "-inf", "+inf")
    .execute()
    .assertThat(array(string("a"), string("b"), string("c")));
  }

}
