package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.Database;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class KeysCommandTest {

    private final IDatabase db = new Database(new HashMap<String, DatabaseValue>());

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Captor
    private ArgumentCaptor<Collection<String>> captor;

    @Test
    public void testExecute() {
        when(request.getParam(0)).thenReturn("a??");

        db.put("abc", string("1"));
        db.put("acd", string("2"));
        db.put("c", string("3"));

        KeysCommand command = new KeysCommand();

        command.execute(db, request, response);

        verify(response).addArray(captor.capture());

        Collection<String> value = captor.getValue();

        assertThat(value.size(), is(2));
        assertThat(value.contains("abc"), is(true));
        assertThat(value.contains("acd"), is(true));
        assertThat(value.contains("c"), is(false));
    }

}
