package tonivade.db.command.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class MultiSetCommandTest {

    @Mock
    private IDatabase db;

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

        verify(db).merge(eq("a"), eq(string("1")), any());
        verify(db).merge(eq("b"), eq(string("2")), any());
        verify(db).merge(eq("c"), eq(string("3")), any());
        verifyNoMoreInteractions(db);

        verify(response).addSimpleStr("OK");
    }

}
