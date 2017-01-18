/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.list;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.list;
import static org.mockito.Matchers.startsWith;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.list.ListIndexCommand;

@CommandUnderTest(ListIndexCommand.class)
public class ListIndexCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "0")
            .execute()
            .verify().addBulkStr(safeString("a"));

        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "-1")
            .execute()
            .verify().addBulkStr(safeString("c"));

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
