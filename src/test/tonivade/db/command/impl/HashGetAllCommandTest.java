package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import java.util.HashMap;
import java.util.Map;

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
public class HashGetAllCommandTest {

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

        db.put("a", hash(
                entry("key1", "value1"),
                entry("key2", "value2"),
                entry("key3", "value3")));

        HashGetAllCommand command = new HashGetAllCommand();

        command.execute(db, request, response);

        verify(response).addValue(captor.capture());

        DatabaseValue value = captor.getValue();

        Map<String, String> map = value.getValue();

        assertThat(map.get("key1"), is("value1"));
        assertThat(map.get("key2"), is("value2"));
        assertThat(map.get("key3"), is("value3"));
    }

}
