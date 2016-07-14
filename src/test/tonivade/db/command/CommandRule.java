/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tonivade.db.DatabaseKeyMatchers.safeKey;
import static tonivade.redis.protocol.SafeString.safeAsList;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.Optional;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import tonivade.db.ITinyDB;
import tonivade.db.TinyDBServerState;
import tonivade.db.TinyDBSessionState;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.command.IServerContext;
import tonivade.redis.command.ISession;

public class CommandRule implements TestRule {

    private IRequest request;

    private IResponse response;

    private ITinyDB server;

    private ISession session;

    private ITinyDBCommand command;

    private final Object target;

    private final TinyDBServerState serverState = new TinyDBServerState(1);

    private final TinyDBSessionState sessionState = new TinyDBSessionState();

    public CommandRule(Object target) {
        this.target = target;
    }

    public IRequest getRequest() {
        return request;
    }

    public IResponse getResponse() {
        return response;
    }

    public IDatabase getDatabase() {
        return serverState.getDatabase(0);
    }

    public ISession getSession() {
        return session;
    }

    public ITinyDB getServer() {
        return server;
    }

    public IDatabase getAdminDatabase() {
        return serverState.getAdminDatabase();
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                server = mock(ITinyDB.class);
                request = mock(IRequest.class);
                response = mock(IResponse.class, new Answer<IResponse>() {
                    @Override
                    public IResponse answer(InvocationOnMock invocation) throws Throwable {
                        return (IResponse) invocation.getMock();
                    }
                });
                session = mock(ISession.class);

                when(request.getServerContext()).thenReturn(server);
                when(request.getSession()).thenReturn(session);
                when(session.getId()).thenReturn("localhost:12345");
                when(session.getValue("state")).thenReturn(sessionState);
                when(server.getAdminDatabase()).thenReturn(serverState.getAdminDatabase());
                when(server.isMaster()).thenReturn(true);
                when(server.getValue("state")).thenReturn(serverState);

                MockitoAnnotations.initMocks(target);

                command = target.getClass().getAnnotation(CommandUnderTest.class).value().newInstance();

                base.evaluate();

                getDatabase().clear();
            }
        };
    }

    public CommandRule withData(String key, DatabaseValue value) {
        withData(getDatabase(), safeKey(key), value);
        return this;
    }

    public CommandRule withData(DatabaseKey key, DatabaseValue value) {
        withData(getDatabase(), key, value);
        return this;
    }

    public CommandRule withAdminData(String key, DatabaseValue value) {
        withData(getAdminDatabase(), safeKey(key), value);
        return this;
    }

    private void withData(IDatabase database, DatabaseKey key, DatabaseValue value) {
        database.put(key, value);
    }

    public CommandRule execute() {
        Mockito.reset(response);
        new TinyDBCommandWrapper(command).execute(request, response);
        return this;
    }

    public CommandRule withParams(String ... params) {
        if (params != null) {
            when(request.getParams()).thenReturn(safeAsList(params));
            int i = 0;
            for (String param : params) {
                when(request.getParam(i++)).thenReturn(safeString(param));
            }
            when(request.getLength()).thenReturn(params.length);
            when(request.getOptionalParam(anyInt())).thenAnswer(invocation -> {
                    Integer param = (Integer) invocation.getArguments()[0];
                    if (param < params.length) {
                        return Optional.of(safeString(params[param]));
                    }
                    return Optional.empty();
                });
        }
        return this;
    }

    public CommandRule assertValue(String key, Matcher<DatabaseValue> matcher) {
        assertValue(getDatabase(), safeKey(key), matcher);
        return this;
    }

    public CommandRule assertAdminValue(String key, Matcher<DatabaseValue> matcher) {
        assertValue(getAdminDatabase(), safeKey(key), matcher);
        return this;
    }

    public CommandRule assertKey(String key, Matcher<DatabaseKey> matcher) {
        assertKey(getDatabase(), safeKey(key), matcher);
        return this;
    }

    public CommandRule assertAdminKey(String key, Matcher<DatabaseKey> matcher) {
        assertKey(getAdminDatabase(), safeKey(key), matcher);
        return this;
    }

    private void assertKey(IDatabase database, DatabaseKey key, Matcher<DatabaseKey> matcher) {
        Assert.assertThat(database.getKey(key), matcher);
    }

    private void assertValue(IDatabase database, DatabaseKey key, Matcher<DatabaseValue> matcher) {
        Assert.assertThat(database.get(key), matcher);
    }

    public IResponse verify() {
        return Mockito.verify(response);
    }

    @SuppressWarnings("unchecked")
    public <T> T verify(Class<T> type) {
        if (type.equals(IServerContext.class)) {
            return (T) Mockito.verify(server);
        } else if (type.equals(ITinyDB.class)) {
                return (T) Mockito.verify(server);
        } else if (type.equals(ISession.class)) {
            return (T) Mockito.verify(session);
        }
        return (T) verify();
    }

    public TinyDBServerState getServerState() {
        return serverState;
    }

    public TinyDBSessionState getSessionState() {
        return sessionState;
    }

}
