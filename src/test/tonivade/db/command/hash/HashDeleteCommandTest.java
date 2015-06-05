/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.hash;

import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.command.hash.HashDeleteCommand;

@CommandUnderTest(HashDeleteCommand.class)
public class HashDeleteCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", hash(entry("a", "1")))
            .withParams("key", "a", "b", "c")
            .execute()
            .verify().addInt(true);
    }

    @Test
    public void testExecuteNoKeys() throws Exception {
        rule.withData("key", hash(entry("d", "1")))
            .withParams("key", "a", "b", "c")
            .execute()
            .verify().addInt(false);
    }

}
