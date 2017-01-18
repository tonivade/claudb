/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.zset;

import static com.github.tonivade.tinydb.DatabaseValueMatchers.score;
import static com.github.tonivade.tinydb.data.DatabaseValue.zset;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.zset.SortedSetRemoveCommand;

@CommandUnderTest(SortedSetRemoveCommand.class)
public class SortedSetRemoveCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "a")
            .execute()
            .assertValue("key", is(zset(score(2.0F, "b"), score(3.0F, "c"))))
            .verify().addInt(1);

        rule.withParams("key", "a")
            .execute()
            .assertValue("key", is(zset(score(2.0F, "b"), score(3.0F, "c"))))
            .verify().addInt(0);
    }

}
