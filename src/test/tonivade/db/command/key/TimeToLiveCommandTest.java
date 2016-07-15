/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.string;
import static tonivade.redis.protocol.SafeString.safeString;

import java.time.Instant;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.data.DatabaseKey;

public abstract class TimeToLiveCommandTest {
    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecuteWithNoExpiration() {
        rule.withData(safeKey(safeString("test")), string("value"))
            .withParams("test")
            .execute()
            .verify().addInt(-1);
    }

    @Test
    public void testExecuteExpired() throws InterruptedException {
        Instant now = Instant.now();

        rule.withData(new DatabaseKey(safeString("test"), now.minusSeconds(10)), string("value"))
            .withParams("test")
            .execute()
            .verify().addInt(-2);
    }
}
