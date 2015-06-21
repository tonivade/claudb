/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static tonivade.db.redis.SafeString.fromString;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(IncrementByCommand.class)
public class IncrementByCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a", "10")
            .execute()
            .verify().addInt(fromString("10"));

        rule.withParams("a", "10")
            .execute()
            .verify().addInt(fromString("20"));

        rule.withParams("a", "5")
            .execute()
            .verify().addInt(fromString("25"));
    }

}
