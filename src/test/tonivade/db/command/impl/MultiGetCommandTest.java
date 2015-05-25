package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.hamcrest.CoreMatchers;
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
public class MultiGetCommandTest {

    private final IDatabase db = new Database(new HashMap<String, DatabaseValue>());

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Captor
    private ArgumentCaptor<Collection<DatabaseValue>> captor;

    @Test
    public void testExecute() {
        when(request.getParams()).thenReturn(Arrays.asList("a", "b", "c"));

        db.put("a", string("1"));
        db.put("c", string("2"));

        MultiGetCommand command = new MultiGetCommand();

        command.execute(db, request, response);

        verify(response).addArrayValue(captor.capture());

        Collection<DatabaseValue> result = captor.getValue();

        Iterator<DatabaseValue> iterator = result.iterator();
        DatabaseValue a = iterator.next();
        DatabaseValue b = iterator.next();
        DatabaseValue c = iterator.next();

        assertThat(a.getValue(), is("1"));
        assertThat(b, is(CoreMatchers.nullValue()));
        assertThat(c.getValue(), is("2"));
    }

}
