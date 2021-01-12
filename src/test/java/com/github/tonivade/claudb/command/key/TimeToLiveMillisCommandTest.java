/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.claudb.data.DatabaseValue.string;

import java.time.Instant;

import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.claudb.data.DatabaseKey;

@CommandUnderTest(TimeToLiveMillisCommand.class)
public class TimeToLiveMillisCommandTest extends TimeToLiveCommandTest {

  @Test
  public void testExecute() {
    Instant now = Instant.now();

    rule.withData(new DatabaseKey(safeString("test")), string("value").expiredAt(now.plusSeconds(10)))
    .withParams("test")
    .execute()
    .assertThat(org.hamcrest.Matchers.any(RedisToken.class));
  }

}
