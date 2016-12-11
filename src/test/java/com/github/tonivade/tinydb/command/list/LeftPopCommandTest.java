/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.list;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.isList;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.list;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.list.LeftPopCommand;

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
