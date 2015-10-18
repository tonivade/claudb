/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static tonivade.db.DatabaseValueMatchers.entry;
import static tonivade.db.DatabaseValueMatchers.list;
import static tonivade.db.DatabaseValueMatchers.score;
import static tonivade.db.DatabaseValueMatchers.set;
import static tonivade.db.data.DatabaseValue.hash;
import static tonivade.db.data.DatabaseValue.string;
import static tonivade.db.data.DatabaseValue.zset;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

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
    public void testExecuteList() {
        rule.withData("a", list("a", "b", "c"))
            .withParams("a")
            .execute()
            .verify().addSimpleStr("list");
    }

    @Test
    public void testExecuteSet() {
        rule.withData("a", set("a", "b", "c"))
            .withParams("a")
            .execute()
            .verify().addSimpleStr("set");
    }

    @Test
    public void testExecuteZSet() {
        rule.withData("a", zset(score(1.0, "a"), score(2.0, "b"), score(3.0, "c")))
            .withParams("a")
            .execute()
            .verify().addSimpleStr("zset");
    }

    @Test
    public void testExecuteNotExists() {
        rule.withData("a", string("string"))
            .withParams("b").execute()
            .verify().addSimpleStr("none");
    }

}
