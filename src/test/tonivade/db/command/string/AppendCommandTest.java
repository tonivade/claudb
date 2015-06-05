/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.command.string.AppendCommand;

@CommandUnderTest(AppendCommand.class)
public class AppendCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("test", string("Hola"))
            .withParams("test", " mundo").execute()
            .verify().addInt(10);
    }

    @Test
    public void testExecuteNoExists() {
        rule.withParams("test", " mundo")
            .execute()
            .verify().addInt(6);
    }

}
