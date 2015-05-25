package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

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
public class GetSetCommandTest {

    private final IDatabase db = new Database(new HashMap<String, DatabaseValue>());

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Captor
    private ArgumentCaptor<DatabaseValue> captor;

    @Test
    public void testExecute() {
        when(request.getParam(0)).thenReturn("a");
        when(request.getParam(1)).thenReturn("2");

        db.put("a", string("1"));

        GetSetCommand command = new GetSetCommand();

        command.execute(db, request, response);

        verify(response).addValue(captor.capture());

        DatabaseValue value = captor.getValue();

        assertThat(value.getValue(), is("1"));
    }

}
