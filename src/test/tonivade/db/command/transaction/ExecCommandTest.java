package tonivade.db.command.transaction;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.Request;

import tonivade.db.TransactionState;
import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(ExecCommand.class)
public class ExecCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<?>> captor;

    private final ICommand command = mock(ICommand.class);

    @Test
    public void executeWithActiveTransaction() throws Exception {
        givenPingCommand();
        givenExistingTransaction();

        rule.execute().verify().addArray(captor.capture());

        verify(command, times(3)).execute(any(), any());

        Collection<?> value = captor.getValue();

        assertThat(value, hasSize(3));
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
