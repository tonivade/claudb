/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.key;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static org.mockito.AdditionalMatchers.and;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.AdditionalMatchers.lt;

import java.time.Instant;

import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.data.DatabaseKey;

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
