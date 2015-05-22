package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class HashGetAllCommandTest {

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Captor
    private ArgumentCaptor<Collection<String>> captor;

    @Test
    public void testExecute() {
        when(request.getParam(0)).thenReturn("a");
        when(db.get("a")).thenReturn(new DatabaseValue(DataType.HASH, map()));

        HashGetAllCommand command = new HashGetAllCommand();

        command.execute(db, request, response);

        verify(response).addArray(captor.capture());

        Collection<String> value = captor.getValue();

        Iterator<String> iterator = value.iterator();
        String key1 = iterator.next();
        String value1 = iterator.next();
        String key2 = iterator.next();
        String value2 = iterator.next();
        String key3 = iterator.next();
        String value3 = iterator.next();

        assertThat(key1, is("key1"));
        assertThat(value1, is("value1"));
        assertThat(key2, is("key2"));
        assertThat(value2, is("value2"));
        assertThat(key3, is("key3"));
        assertThat(value3, is("value3"));
    }

    private HashMap<String, String> map() {
        HashMap<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        return map;
    }

}
