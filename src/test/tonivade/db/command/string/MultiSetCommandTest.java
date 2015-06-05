/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.command.string.MultiSetCommand;

@CommandUnderTest(MultiSetCommand.class)
public class MultiSetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a", "1", "b", "2", "c", "3")
            .execute()
            .assertThat("a", is(string("1")))
            .assertThat("b", is(string("2")))
            .assertThat("c", is(string("3")))
            .verify().addSimpleStr("OK");

        assertThat(rule.getDatabase().size(), is(3));
    }

}
