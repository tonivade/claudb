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

@CommandUnderTest(SortedSetRemoveCommand.class)
public class SortedSetRemoveCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
    .withParams("key", "a")
    .execute()
    .assertValue("key", is(zset(score(2.0F, "b"), score(3.0F, "c"))))
    .assertThat(RedisToken.integer(1));

    rule.withParams("key", "a")
    .execute()
    .assertValue("key", is(zset(score(2.0F, "b"), score(3.0F, "c"))))
    .assertThat(RedisToken.integer(0));
  }

}
