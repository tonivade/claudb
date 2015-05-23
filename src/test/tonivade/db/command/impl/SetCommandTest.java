package tonivade.db.command.impl;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import java.util.function.BiFunction;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class SetCommandTest {

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() {
        when(request.getParam(0)).thenReturn("a");
        when(request.getParam(1)).thenReturn("1");

        SetCommand command = new SetCommand();

        command.execute(db, request, response);

        verify(db).merge(
                eq("a"),
                argThat(new BaseMatcher<DatabaseValue>() {
                            @Override
                            public void describeTo(Description description) {
                                description.appendText("value expected: \"1\"");
                            }

                            @Override
                            public boolean matches(Object value) {
                                DatabaseValue db = (DatabaseValue) value;
                                return db.getType() == DataType.STRING && db.getValue().equals("1");
                            }
                        }),
                argThat(new BaseMatcher<BiFunction<DatabaseValue, DatabaseValue, DatabaseValue>>() {
                            @Override
                            public void describeTo(Description description) {
                                description.appendText("value expected: \"1\"");
                            }

                            @Override
                            public boolean matches(Object value) {
                                BiFunction<DatabaseValue, DatabaseValue, DatabaseValue> function =
                                        (BiFunction<DatabaseValue, DatabaseValue, DatabaseValue>) value;
                                DatabaseValue db = function.apply(string("0"), string("1"));
                                return db.getValue().equals("1");
                            }
                        }));
        verify(response).addSimpleStr("OK");
    }

}
