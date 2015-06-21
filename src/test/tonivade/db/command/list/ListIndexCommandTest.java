/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static org.mockito.Matchers.startsWith;
import static tonivade.db.data.DatabaseValue.list;
import static tonivade.db.redis.SafeString.fromString;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(ListIndexCommand.class)
public class ListIndexCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "0")
            .execute()
            .verify().addBulkStr(fromString("a"));

        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "-1")
            .execute()
            .verify().addBulkStr(fromString("c"));

        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "-4")
            .execute()
            .verify().addBulkStr(null);

        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "4")
            .execute()
            .verify().addBulkStr(null);

        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "a")
            .execute()
            .verify().addError(startsWith("ERR"));
    }

}
