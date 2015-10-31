/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.server.command.ISession;

@CommandUnderTest(SelectCommand.class)
public class SelectCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("10")
            .execute()
            .verify(ISession.class).setCurrentDB(10);

        rule.withParams("asdfsdf")
            .execute()
            .verify().addError("ERR invalid DB index");
    }

}
