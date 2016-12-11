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

@CommandUnderTest(SetBitCommand.class)
public class SetBitCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecuteOne() throws Exception {
        rule.withData("test", DatabaseValue.bitset())
            .withParams("test", "10", "1")
            .execute()
            .verify().addInt(false);
    }

    @Test
    public void testExecuteZero() throws Exception {
        rule.withData("test", DatabaseValue.bitset(10))
            .withParams("test", "10", "0")
            .execute()
            .verify().addInt(true);
    }

    @Test
    public void testExecuteBitFormat() throws Exception {
        rule.withData("test", DatabaseValue.bitset())
            .withParams("test", "1", "a")
            .execute()
            .verify().addError("bit or offset is not an integer");
    }

    @Test
    public void testExecuteOffsetFormat() throws Exception {
        rule.withData("test", DatabaseValue.bitset())
            .withParams("test", "a", "0")
            .execute()
            .verify().addError("bit or offset is not an integer");
    }
}
