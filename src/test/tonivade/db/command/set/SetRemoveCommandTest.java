/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import static org.hamcrest.CoreMatchers.is;
import static tonivade.db.data.DatabaseValue.set;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(SetRemoveCommand.class)
public class SetRemoveCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", set("a", "b", "c"))
            .withParams("key", "a")
            .execute()
            .assertThat("key", is(set("b", "c")))
            .verify().addInt(1);

        rule.withParams("key", "a")
            .execute()
            .assertThat("key", is(set("b", "c")))
            .verify().addInt(0);
    }

}
