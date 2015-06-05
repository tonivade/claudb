/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.command.string.DecrementByCommand;

@CommandUnderTest(DecrementByCommand.class)
public class DecrementByCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a", "10")
            .execute()
            .verify().addInt("-10");

        rule.withParams("a", "10")
            .execute()
            .verify().addInt("-20");

        rule.withParams("a", "5")
            .execute()
            .verify().addInt("-25");
    }

}
