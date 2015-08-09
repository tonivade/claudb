/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static tonivade.db.DatabaseValueMatchers.isList;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(RightPushCommand.class)
public class RightPushCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("key", "a", "b", "c")
            .execute()
            .assertValue("key", isList("a", "b", "c"))
            .verify().addInt(3);

        rule.withParams("key", "d")
            .execute()
            .assertValue("key", isList("a", "b", "c", "d"))
            .verify().addInt(4);
    }

}
