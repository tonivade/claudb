/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.server;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.server.SelectCommand;

@CommandUnderTest(SelectCommand.class)
public class SelectCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("10")
            .execute();

        assertThat(rule.getSessionState().getCurrentDB(), is(10));
    }

    @Test
    public void testExecuteWithInvalidParam() {
        rule.withParams("asdfsdf")
            .execute()
            .verify().addError("ERR invalid DB index");
    }

}
