/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.zset;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.score;
import static com.github.tonivade.tinydb.data.DatabaseValue.zset;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(SortedSetRangeCommand.class)
public class SortedSetRangeCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Captor
  private ArgumentCaptor<Collection<?>> captor;

  @Test
  public void testExecute()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "0", "-1")
    .execute()
    .then(array(string("a"), string("b"), string("c")));
  }

  @Test
  public void testExecuteWithScores()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "0", "-1", "WITHSCORES")
    .execute()
    .then(array(string("a"), string("1.0"), 
        string("b"), string("2.0"), 
        string("c"), string("3.0")));
  }

  @Test
  public void testExecuteHead()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "0", "1")
    .execute()
    .then(array(string("a"), string("b")));
  }

  @Test
  public void testExecuteTail()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "-2", "-1")
    .execute()
    .then(array(string("b"), string("c")));
  }

  @Test
  public void testExecuteToOutOfRange()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "1", "4")
    .execute()
    .then(array(string("b"), string("c")));
  }

  @Test
  public void testExecuteFromOutOfRange()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "4", "6")
    .execute()
    .then(RedisToken.array());
  }

  @Test
  public void testExecuteFromOrder()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "-1", "0")
    .execute()
    .then(RedisToken.array());
  }

  @Test
  public void testExecuteOne()  {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "0", "0")
    .execute()
    .then(array(string("a")));
  }

  @Test
  public void testExecuteNoExists()  {
    rule.withParams("key", "0", "-1")
    .execute()
    .then(RedisToken.array());
  }

}
