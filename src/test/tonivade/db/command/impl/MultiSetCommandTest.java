package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import java.util.Arrays;
import java.util.HashMap;

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
public class MultiSetCommandTest {

    private final IDatabase db = new Database(new HashMap<String, DatabaseValue>());

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() {
        when(request.getParams()).thenReturn(
                Arrays.asList("a", "1", "b", "2", "c", "3"));

        MultiSetCommand command = new MultiSetCommand();

        command.execute(db, request, response);

        assertThat(db.get("a"), is(string("1")));
        assertThat(db.get("b"), is(string("2")));
        assertThat(db.get("c"), is(string("3")));
        assertThat(db.size(), is(3));

        verify(response).addSimpleStr("OK");
    }

}
