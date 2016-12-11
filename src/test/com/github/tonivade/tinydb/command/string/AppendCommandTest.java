/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.string.AppendCommand;

@CommandUnderTest(AppendCommand.class)
public class AppendCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("test", string("Hola"))
            .withParams("test", " mundo").execute()
            .verify().addInt(10);
    }

    @Test
    public void testExecuteNoExists() {
        rule.withParams("test", " mundo")
            .execute()
            .verify().addInt(6);
    }

}
