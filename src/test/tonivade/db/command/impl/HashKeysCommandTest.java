package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class HashKeysCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<String>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.getDatabase().put("key", hash(entry("a", "1"), entry("b", "2")));

        rule.withParams("key", "a").execute(new HashKeysCommand());

        verify(rule.getResponse()).addArray(captor.capture());

        Collection<String> keys = captor.getValue();

        assertThat(keys.contains("a"), is(true));
        assertThat(keys.contains("b"), is(true));
    }

}
