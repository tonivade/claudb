/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static tonivade.db.DatabaseValueMatchers.set;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.protocol.SafeString;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(SetMembersCommand.class)
public class SetMembersCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<SafeString>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", set("a", "b", "c"))
            .withParams("key")
            .execute()
            .verify().addArray(captor.capture());

        Collection<SafeString> value = captor.getValue();

        assertThat(value, contains(safeString("a"), safeString("b"), safeString("c")));
    }
}
