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

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(SortedSetRemoveCommand.class)
public class SortedSetRemoveCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "a")
            .execute()
            .assertThat("key", is(zset(score(2.0F, "b"), score(3.0F, "c"))))
            .verify().addInt(1);

        rule.withParams("key", "a")
            .execute()
            .assertThat("key", is(zset(score(2.0F, "b"), score(3.0F, "c"))))
            .verify().addInt(0);
    }

}
