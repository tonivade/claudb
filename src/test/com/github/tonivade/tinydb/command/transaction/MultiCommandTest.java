package com.github.tonivade.tinydb.command.transaction;

import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.TransactionState;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.transaction.MultiCommand;

@CommandUnderTest(MultiCommand.class)
public class MultiCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void executeWithoutActiveTransaction() throws Exception {
        rule.execute()
            .verify().addSimpleStr("OK");
    }

    @Test
    public void executeWithActiveTransaction() throws Exception {
        when(rule.getSession().getValue("tx")).thenReturn(new TransactionState());

        rule.execute()
            .verify().addError("ERR MULTI calls can not be nested");
    }
}
