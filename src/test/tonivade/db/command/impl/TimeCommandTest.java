package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class TimeCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<String>> captor;

    @Test
    public void testExecute() {
        rule.execute(new TimeCommand());

        verify(rule.getResponse()).addArray(captor.capture());

        Collection<String> value = captor.getValue();

        Iterator<String> iterator = value.iterator();
        String secs = iterator.next();
        String mics = iterator.next();

        System.out.println(secs);
        System.out.println(mics);
    }

}
