package tonivade.db.command.transaction;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.redis.protocol.SafeString.safeString;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.TransactionState;
import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.redis.command.ICommand;
import tonivade.redis.command.Request;

@CommandUnderTest(ExecCommand.class)
public class ExecCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    private final ICommand command = mock(ICommand.class);

    @Test
    public void executeWithActiveTransaction() throws Exception {
        givenPingCommand();
        givenExistingTransaction();

        rule.execute();

        verify(command, times(3)).execute(any(), any());
    }

    @Test
    public void executeWithoutActiveTransaction() throws Exception {
        rule.execute()
            .verify().addError("ERR EXEC without MULTI");
    }

    private void givenPingCommand() {
        when(rule.getServer().getCommand("ping")).thenReturn(command);
    }

    private void givenExistingTransaction() {
        TransactionState transaction = createTransaction();
        TransactionState noTransaction = null;

        when(rule.getSession().getValue("tx")).thenReturn(transaction, noTransaction);
        when(rule.getSession().removeValue("tx")).thenReturn(transaction);
    }

    private TransactionState createTransaction() {
        TransactionState transaction = new TransactionState();
        transaction.enqueue(new Request(null, null, safeString("ping"), null));
        transaction.enqueue(new Request(null, null, safeString("ping"), null));
        transaction.enqueue(new Request(null, null, safeString("ping"), null));
        return transaction;
    }
}
