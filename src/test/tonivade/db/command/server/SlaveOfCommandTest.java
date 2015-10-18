/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.TinyDBRule;
import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(SlaveOfCommand.class)
public class SlaveOfCommandTest {

    @Rule
    public final TinyDBRule server = new TinyDBRule("localhost", 8081);

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("localhost", "8081")
            .execute()
            .verify().addSimpleStr("OK");
    }

}
