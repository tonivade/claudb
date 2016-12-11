/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.server;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.TinyDBRule;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.server.SlaveOfCommand;

@CommandUnderTest(SlaveOfCommand.class)
public class SlaveOfCommandTest {

    @Rule
    public final TinyDBRule server = new TinyDBRule("localhost", 34241);

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("localhost", "34241")
            .execute()
            .verify().addSimpleStr("OK");
    }

}
