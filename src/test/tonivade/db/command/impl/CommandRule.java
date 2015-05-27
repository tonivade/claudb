package tonivade.db.command.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.MockitoAnnotations;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.Database;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

public class CommandRule implements TestRule {

    private IRequest request;

    private IResponse response;

    private IDatabase database;

    private final Object target;

    public CommandRule(Object target) {
        super();
        this.target = target;
    }

    public IRequest getRequest() {
        return request;
    }

    public IResponse getResponse() {
        return response;
    }

    public IDatabase getDatabase() {
        return database;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                request = mock(IRequest.class);
                response = mock(IResponse.class);
                database = new Database(new HashMap<String, DatabaseValue>());

                MockitoAnnotations.initMocks(target);

                base.evaluate();

                database.clear();
            }
        };
    }

    public void execute(ICommand command) {
        command.execute(database, request, response);
    }

    public CommandRule withParams(String ... params) {
        if (params != null) {
            when(request.getParams()).thenReturn(Arrays.asList(params));
            int i = 0;
            for (String param : params) {
                when(request.getParam(i++)).thenReturn(param);
            }
            when(request.getLength()).thenReturn(params.length);
        }
        return this;
    }

}
