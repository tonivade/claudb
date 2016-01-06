/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(SelectCommand.class)
public class SelectCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("10")
            .execute();

        assertThat(rule.getSessionState().getCurrentDB(), is(10));
    }

    @Test
    public void testExecuteWithInvalidParam() {
        rule.withParams("asdfsdf")
            .execute()
            .verify().addError("ERR invalid DB index");
    }

}
