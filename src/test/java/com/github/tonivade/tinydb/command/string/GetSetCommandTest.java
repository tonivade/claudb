/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.string.GetSetCommand;

@CommandUnderTest(GetSetCommand.class)
public class GetSetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<SafeString> captor;

    @Test
    public void testExecute() {
        rule.withData("a", string("1"))
            .withParams("a", "2")
            .execute()
            .verify().addBulkStr(captor.capture());

        assertThat(captor.getValue(), is(safeString("1")));
    }

}
