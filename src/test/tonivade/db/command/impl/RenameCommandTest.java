package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.Database;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class RenameCommandTest {

    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Before
    public void setUp() throws Exception {
        db = new Database(new HashMap<String, DatabaseValue>());
    }

    @Test
    public void testExecute() {
        when(request.getParam(0)).thenReturn("a");
        when(request.getParam(1)).thenReturn("b");

        db.put("a", string("1"));

        RenameCommand command = new RenameCommand();

        command.execute(db, request, response);

        assertThat(db.get("a"), is(nullValue()));
        assertThat(db.get("b"), is(string("1")));

        verify(response).addSimpleStr("OK");
    }

    @Test
    public void testExecuteError() {
        when(request.getParam(0)).thenReturn("a");
        when(request.getParam(1)).thenReturn("b");

        RenameCommand command = new RenameCommand();

        command.execute(db, request, response);

        assertThat(db.get("a"), is(nullValue()));
        assertThat(db.get("b"), is(nullValue()));

        verify(response).addError("ERR no such key");
    }

}
