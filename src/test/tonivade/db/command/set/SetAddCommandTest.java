/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import static org.hamcrest.CoreMatchers.is;
import static tonivade.db.data.DatabaseValue.setFromString;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(SetAddCommand.class)
public class SetAddCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("key", "value")
            .execute()
            .assertThat("key", is(setFromString("value")))
            .verify().addInt(1);
    }

}
