/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.command.key.RenameCommand;

@CommandUnderTest(RenameCommand.class)
public class RenameCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("a", string("1"))
            .withParams("a", "b")
            .execute()
            .assertValue("a", is(nullValue()))
            .assertValue("b", is(string("1")))
            .verify().addSimpleStr("OK");
    }

    @Test
    public void testExecuteError() {
        rule.withParams("a", "b")
            .execute()
            .assertValue("a", is(nullValue()))
            .assertValue("b", is(nullValue()))
            .verify().addError("ERR no such key");
    }

}
