/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.zset;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.score;
import static com.github.tonivade.tinydb.data.DatabaseValue.zset;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(SortedSetRangeByScoreCommand.class)
public class SortedSetRangeByScoreCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() throws Exception {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "1", "3")
    .execute()
    .then(array(string("a"), string("b"), string("c")));
  }

  @Test
  public void testExecuteWithScores() throws Exception {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "1", "3", "WITHSCORES")
    .execute()
    .then(array(string("a"), string("1.0"), 
        string("b"), string("2.0"), 
        string("c"), string("3.0")));
  }

  @Test
  public void testExecuteWithLimit() throws Exception {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "1", "3", "LIMIT", "1", "2")
    .execute()
    .then(array(string("b"), string("c")));
  }

  @Test
  public void testExecuteExclusive() throws Exception {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "(1", "3")
    .execute()
    .then(array(string("b"), string("c")));
  }

  @Test
  public void testExecuteInfinity() throws Exception {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "-inf", "+inf")
    .execute()
    .then(array(string("a"), string("b"), string("c")));
  }

}
