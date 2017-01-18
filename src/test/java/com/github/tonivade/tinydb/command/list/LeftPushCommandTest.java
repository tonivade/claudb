/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.list;

import static com.github.tonivade.tinydb.DatabaseValueMatchers.isList;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.list.LeftPushCommand;

@CommandUnderTest(LeftPushCommand.class)
public class LeftPushCommandTest {

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
            .assertValue("key", isList("d", "a", "b", "c"))
            .verify().addInt(4);
    }

}
