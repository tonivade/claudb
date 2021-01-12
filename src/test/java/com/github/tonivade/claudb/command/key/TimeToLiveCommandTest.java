/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;

import java.time.Instant;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.data.DatabaseKey;

public abstract class TimeToLiveCommandTest {
    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecuteWithNoExpiration() {
        rule.withData(safeKey(safeString("test")), string("value"))
            .withParams("test")
            .execute()
            .assertThat(RedisToken.integer(-1));
    }

    @Test
    public void testExecuteExpired() {
        Instant now = Instant.now();

        rule.withData(new DatabaseKey(safeString("test")), string("value").expiredAt(now.minusSeconds(10)))
            .withParams("test")
            .execute()
            .assertThat(RedisToken.integer(-2));
    }
}
