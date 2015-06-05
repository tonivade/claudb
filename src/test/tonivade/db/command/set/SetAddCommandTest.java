/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.command.set.SetAddCommand;
import tonivade.db.data.DatabaseValue;

@CommandUnderTest(SetAddCommand.class)
public class SetAddCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("key", "value")
            .execute()
            .assertThat("key", CoreMatchers.is(DatabaseValue.set("value")))
            .verify().addInt(1);
    }

}
