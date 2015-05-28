package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.string;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@CommandUnderTest(KeysCommand.class)
public class KeysCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<String>> captor;

    @Test
    public void testExecute() {
        rule.withData("abc", string("1"))
            .withData("acd", string("2"))
            .withData("c", string("3"))
            .withParams("a??")
            .execute()
            .verify().addArray(captor.capture());

        Collection<String> value = captor.getValue();

        assertThat(value.size(), is(2));
        assertThat(value.contains("abc"), is(true));
        assertThat(value.contains("acd"), is(true));
        assertThat(value.contains("c"), is(false));
    }

}
