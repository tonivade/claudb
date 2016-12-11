/*
 * Copyright (c) 2016, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.bitset;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.data.DatabaseValue;


@CommandUnderTest(GetBitCommand.class)
public class GetBitCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecuteOne() throws Exception {
        rule.withData("test", DatabaseValue.bitset(10))
            .withParams("test", "10")
            .execute()
            .verify().addInt(true);
    }

    @Test
    public void testExecuteZero() throws Exception {
        rule.withData("test", DatabaseValue.bitset())
            .withParams("test", "10")
            .execute()
            .verify().addInt(false);
    }

    @Test
    public void testExecuteFormat() throws Exception {
        rule.withData("test", DatabaseValue.bitset())
            .withParams("test", "a")
            .execute()
            .verify().addError("bit offset is not an integer");
    }

}
