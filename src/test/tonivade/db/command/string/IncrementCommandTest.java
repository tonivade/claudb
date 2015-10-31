/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static tonivade.redis.protocol.SafeString.safeString;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(IncrementCommand.class)
public class IncrementCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a")
            .execute()
            .verify().addInt(safeString("1"));

        rule.withParams("a")
            .execute()
            .verify().addInt(safeString("2"));

        rule.withParams("a")
            .execute()
            .verify().addInt(safeString("3"));
    }

}
