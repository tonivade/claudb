/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.claudb.DatabaseValueMatchers.nullValue;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

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
            .assertThat(RedisToken.status("OK"));
    }

    @Test
    public void testExecuteError() {
        rule.withParams("a", "b")
            .execute()
            .assertValue("a", is(nullValue()))
            .assertValue("b", is(nullValue()))
            .assertThat(RedisToken.error("ERR no such key"));
    }

}
