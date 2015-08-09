/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.zset;

import static tonivade.db.DatabaseValueMatchers.score;
import static tonivade.db.data.DatabaseValue.zset;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

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
