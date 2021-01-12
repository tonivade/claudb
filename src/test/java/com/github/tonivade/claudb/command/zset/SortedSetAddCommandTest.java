/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.zset;

import static com.github.tonivade.claudb.DatabaseValueMatchers.score;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Rule;
import org.junit.Test;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.resp.protocol.RedisToken;

@CommandUnderTest(SortedSetAddCommand.class)
public class SortedSetAddCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withParams("key", "1", "one")
    .execute()
    .assertValue("key", is(zset(score(1.0, "one"))))
    .assertThat(RedisToken.integer(1));

    rule.withParams("key", "2", "two")
    .execute()
    .assertValue("key", is(zset(
        score(1.0, "one"),
        score(2.0, "two"))))
    .assertThat(RedisToken.integer(1));

    rule.withParams("key", "2", "one")
    .execute()
    .assertValue("key", is(zset(
        score(1.0, "one"),
        score(2.0, "two"))))
    .assertThat(RedisToken.integer(0));
  }

}
