/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.key;

import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.key.ExistsCommand;

@CommandUnderTest(ExistsCommand.class)
public class ExistsCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("test", string("value"))
            .withParams("test")
            .execute()
            .verify().addInt(true);
    }

    @Test
    public void testExecuteNotExists() {
        rule.withParams("test")
            .execute()
            .verify().addInt(false);
    }

}
