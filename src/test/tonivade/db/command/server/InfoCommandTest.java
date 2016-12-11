/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import static org.mockito.Matchers.any;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.SafeString;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(InfoCommand.class)
public class InfoCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams()
            .execute()
            .verify().addBulkStr(any(SafeString.class));
    }
}
