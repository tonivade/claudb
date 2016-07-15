/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static org.mockito.AdditionalMatchers.and;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.AdditionalMatchers.lt;
import static tonivade.db.data.DatabaseValue.string;
import static tonivade.redis.protocol.SafeString.safeString;

import java.time.Instant;

import org.junit.Test;

import tonivade.db.command.CommandUnderTest;
import tonivade.db.data.DatabaseKey;

@CommandUnderTest(TimeToLiveMillisCommand.class)
public class TimeToLiveMillisCommandTest extends TimeToLiveCommandTest {

    @Test
    public void testExecute() throws InterruptedException {
        Instant now = Instant.now();

        rule.withData(new DatabaseKey(safeString("test"), now.plusSeconds(10)), string("value"))
            .withParams("test")
            .execute()
            .verify().addInt(and(gt(8999), lt(10001)));
    }

}
