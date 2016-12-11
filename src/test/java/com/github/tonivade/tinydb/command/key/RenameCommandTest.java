/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.key;

import static com.github.tonivade.tinydb.DatabaseValueMatchers.nullValue;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(RenameCommand.class)
public class RenameCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("a", string("1"))
            .withParams("a", "b")
            .execute()
            .assertValue("a", is(nullValue()))
            .assertValue("b", is(string("1")))
            .verify().addSimpleStr("OK");
    }

    @Test
    public void testExecuteError() {
        rule.withParams("a", "b")
            .execute()
            .assertValue("a", is(nullValue()))
            .assertValue("b", is(nullValue()))
            .verify().addError("ERR no such key");
    }

}
