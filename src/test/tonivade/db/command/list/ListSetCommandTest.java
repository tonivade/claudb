/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static org.mockito.Matchers.startsWith;
import static tonivade.db.DatabaseValueMatchers.isList;
import static tonivade.db.DatabaseValueMatchers.list;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(ListSetCommand.class)
public class ListSetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "0", "A")
            .execute()
            .assertValue("key", isList("A", "b", "c"))
            .verify().addSimpleStr("OK");

        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "-1", "C")
            .execute()
            .assertValue("key", isList("a", "b", "C"))
            .verify().addSimpleStr("OK");

        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "z", "C")
            .execute()
            .assertValue("key", isList("a", "b", "c"))
            .verify().addError(startsWith("ERR"));

        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "99", "C")
            .execute()
            .assertValue("key", isList("a", "b", "c"))
            .verify().addError(startsWith("ERR"));
    }

}
