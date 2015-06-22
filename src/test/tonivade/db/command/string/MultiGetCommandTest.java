/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.string;
import static tonivade.db.redis.SafeString.safeString;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.data.DatabaseValue;

@CommandUnderTest(MultiGetCommand.class)
public class MultiGetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<DatabaseValue>> captor;

    @Test
    public void testExecute() {
        rule.withData("a", string("1"))
            .withData("c", string("2"))
            .withParams("a", "b", "c")
            .execute()
            .verify().addArrayValue(captor.capture());

        Collection<DatabaseValue> result = captor.getValue();

        Iterator<DatabaseValue> iterator = result.iterator();
        DatabaseValue a = iterator.next();
        DatabaseValue b = iterator.next();
        DatabaseValue c = iterator.next();

        assertThat(a.getValue(), is(safeString("1")));
        assertThat(b, is(nullValue()));
        assertThat(c.getValue(), is(safeString("2")));
    }

}
