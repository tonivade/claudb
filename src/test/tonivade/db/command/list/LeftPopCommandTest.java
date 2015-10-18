/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static tonivade.db.DatabaseValueMatchers.isList;
import static tonivade.db.DatabaseValueMatchers.list;
import static tonivade.db.redis.SafeString.safeString;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(LeftPopCommand.class)
public class LeftPopCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", list("a", "b", "c"))
            .withParams("key")
            .execute()
            .assertValue("key", isList("b", "c"))
            .verify().addBulkStr(safeString("a"));
    }

}
