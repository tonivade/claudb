package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class GetSetCommandTest {

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Captor
    private ArgumentCaptor<DatabaseValue> captor;

    @Test
    public void testExecute() {
        when(db.merge(eq("a"), any(), any())).thenReturn(string("1"));
        when(request.getParam(0)).thenReturn("a");
        when(request.getParam(1)).thenReturn("2");

        GetSetCommand command = new GetSetCommand();

        command.execute(db, request, response);

        verify(response).addValue(captor.capture());

        DatabaseValue value = captor.getValue();

        assertThat(value.getValue(), is("1"));
    }

}
