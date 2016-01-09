/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(ExistsCommand.class)
public class ExistsCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("test", string("value"))
            .withParams("test")
            .execute()
            .verify().addInt(true);
    }

    @Test
    public void testExecuteNotExists() {
        rule.withParams("test")
            .execute()
            .verify().addInt(false);
    }

}
