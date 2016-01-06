/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.notNull;
import static tonivade.db.DatabaseKeyMatchers.safeKey;
import static tonivade.db.DatabaseValueMatchers.set;

import java.util.Set;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.data.DatabaseValue;
import tonivade.redis.protocol.SafeString;

@CommandUnderTest(SetPopCommand.class)
public class SetPopCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", set("a", "b", "c"))
            .withParams("key")
            .execute()
            .verify().addBulkStr(notNull(SafeString.class));

        DatabaseValue value = rule.getDatabase().get(safeKey("key"));
        assertThat(value.<Set<String>>getValue().size(), is(2));
    }

    @Test
    public void testExecuteNotExists() throws Exception {
        rule.withParams("key")
            .execute()
            .verify().addBulkStr(isNull(SafeString.class));
    }

}
