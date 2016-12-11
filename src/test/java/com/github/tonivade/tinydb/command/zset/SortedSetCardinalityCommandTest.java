/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.zset;

import static com.github.tonivade.tinydb.DatabaseValueMatchers.score;
import static com.github.tonivade.tinydb.data.DatabaseValue.zset;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.zset.SortedSetCardinalityCommand;

@CommandUnderTest(SortedSetCardinalityCommand.class)
public class SortedSetCardinalityCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", zset(score(1.0, "a"), score(2.0, "b"), score(3.0, "c")))
            .withParams("key")
            .execute()
            .verify().addInt(3);

        rule.withParams("notExists")
            .execute()
            .verify().addInt(0);
    }

}
