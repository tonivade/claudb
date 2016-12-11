/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.protocol.SafeString;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(GetCommand.class)
public class GetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<SafeString> captor;

    @Test
    public void testExecute() {
        rule.withData("key", string("value"))
            .withParams("key")
            .execute()
            .verify().addBulkStr(captor.capture());

        assertThat(captor.getValue(), is(safeString("value")));
    }

}
