/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.string;
import static tonivade.server.protocol.SafeString.safeString;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.data.DatabaseValue;

@CommandUnderTest(GetSetCommand.class)
public class GetSetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<DatabaseValue> captor;

    @Test
    public void testExecute() {
        rule.withData("a", string("1"))
            .withParams("a", "2")
            .execute()
            .verify().addValue(captor.capture());

        DatabaseValue value = captor.getValue();

        assertThat(value.getValue(), is(safeString("1")));
    }

}
