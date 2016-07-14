/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static org.mockito.AdditionalMatchers.and;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.AdditionalMatchers.lt;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.string;
import static tonivade.redis.protocol.SafeString.safeString;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(TimeToLiveCommand.class)
public class TimeToLiveCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData(safeKey(safeString("test"), 10), string("value"))
            .withParams("test")
            .execute()
            .verify().addInt(and(gt(0), lt(10)));
    }

    @Test
    public void testExecuteExpired() {
        rule.withData(safeKey(safeString("test"), 0), string("value"))
            .withParams("test")
            .execute()
            .verify().addInt(-2);
    }

}
