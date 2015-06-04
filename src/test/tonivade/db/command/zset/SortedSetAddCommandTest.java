/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.zset;

import static org.hamcrest.CoreMatchers.is;
import static tonivade.db.data.DatabaseValue.score;
import static tonivade.db.data.DatabaseValue.zset;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;

@CommandUnderTest(SortedSetAddCommand.class)
public class SortedSetAddCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("key", "1", "one")
            .execute()
            .assertThat("key", is(zset(score(1.0F, "one"))))
            .verify().addInt(1);

        rule.withParams("key", "2", "two")
            .execute()
            .assertThat("key", is(zset(
                    score(1.0F, "one"),
                    score(2.0F, "two"))))
            .verify().addInt(1);

        rule.withParams("key", "1", "one")
            .execute()
            .assertThat("key", is(zset(
                    score(1.0F, "one"),
                    score(2.0F, "two"))))
            .verify().addInt(0);
    }

}
