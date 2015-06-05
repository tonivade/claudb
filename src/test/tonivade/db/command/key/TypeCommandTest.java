/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.command.key.TypeCommand;

@CommandUnderTest(TypeCommand.class)
public class TypeCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecuteString() {
        rule.withData("a", string("string"))
            .withParams("a").execute()
            .verify().addSimpleStr("string");
    }

    @Test
    public void testExecuteHash() {
        rule.withData("a", hash(entry("k1", "v1")))
            .withParams("a")
            .execute()
            .verify().addSimpleStr("hash");
    }

    @Test
    public void testExecuteNotExists() {
        rule.withData("a", string("string"))
            .withParams("b").execute()
            .verify().addSimpleStr("none");
    }

}
