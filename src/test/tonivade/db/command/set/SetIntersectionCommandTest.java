/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.setFromString;
import static tonivade.db.redis.SafeString.safeString;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(SetIntersectionCommand.class)
public class SetIntersectionCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<String>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withData("a", setFromString("1", "2", "3"))
            .withData("b", setFromString("3", "4"))
            .withParams("a", "b")
            .execute()
            .verify().addArray(captor.capture());

        Collection<String> result = captor.getValue();

        assertThat(result.size(), is(1));

        assertThat(result.contains(safeString("3")), is(true));
    }

    @Test
    public void testExecuteNoExists() throws Exception {
        rule.withData("a", setFromString("1", "2", "3"))
            .withParams("a", "b")
            .execute()
            .verify().addArray(captor.capture());

        Collection<String> result = captor.getValue();

        assertThat(result.isEmpty(), is(true));
    }

}
