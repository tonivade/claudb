/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.server;

import static org.mockito.Matchers.any;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.server.SyncCommand;

@CommandUnderTest(SyncCommand.class)
public class SyncCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.execute()
            .verify(ITinyDB.class).exportRDB(any());
    }

}
