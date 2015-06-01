package tonivade.db.command.hash;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.hash.HashKeysCommand;
import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;

@CommandUnderTest(HashKeysCommand.class)
public class HashKeysCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<String>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", hash(entry("a", "1"), entry("b", "2")))
            .withParams("key", "a")
            .execute()
            .verify().addArray(captor.capture());

        Collection<String> keys = captor.getValue();

        assertThat(keys.size(), is(2));
        assertThat(keys.contains("a"), is(true));
        assertThat(keys.contains("b"), is(true));
    }

}
