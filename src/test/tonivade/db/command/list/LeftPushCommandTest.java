/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static org.hamcrest.CoreMatchers.is;
import static tonivade.db.data.DatabaseValue.list;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;
import tonivade.db.command.list.LeftPushCommand;

@CommandUnderTest(LeftPushCommand.class)
public class LeftPushCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("key", "a", "b", "c")
            .execute()
            .assertThat("key", is(list("a", "b", "c")))
            .verify().addInt(3);

        rule.withParams("key", "d")
            .execute()
            .assertThat("key", is(list("d", "a", "b", "c")))
            .verify().addInt(4);
    }

}
