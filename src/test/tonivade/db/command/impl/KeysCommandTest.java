package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class KeysCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<String>> captor;

    @Test
    public void testExecute() {
        when(rule.getRequest().getParam(0)).thenReturn("a??");

        rule.getDatabase().put("abc", string("1"));
        rule.getDatabase().put("acd", string("2"));
        rule.getDatabase().put("c", string("3"));

        rule.execute(new KeysCommand());

        verify(rule.getResponse()).addArray(captor.capture());

        Collection<String> value = captor.getValue();

        assertThat(value.size(), is(2));
        assertThat(value.contains("abc"), is(true));
        assertThat(value.contains("acd"), is(true));
        assertThat(value.contains("c"), is(false));
    }

}
