/*
 * Copyright (c) 2016, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.bitset;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.data.DatabaseValue;

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
