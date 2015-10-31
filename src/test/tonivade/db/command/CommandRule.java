/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tonivade.db.DatabaseKeyMatchers.safeKey;
import static tonivade.server.protocol.SafeString.safeAsList;
import static tonivade.server.protocol.SafeString.safeString;

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
import tonivade.db.RedisServerState;
import tonivade.db.RedisSessionState;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;
import tonivade.server.command.IServerContext;
import tonivade.server.command.ISession;

public class CommandRule implements TestRule {

    private IRequest request;

    private IResponse response;

    private IDatabase database;

    private ITinyDB server;

    private ISession session;

    private IRedisCommand command;

    private final Object target;

    private final RedisServerState redisServerState = new RedisServerState(1);

    private final RedisSessionState redisSessionState = new RedisSessionState();

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
        return database;
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
                database = redisServerState.getDatabase(0);

                when(request.getServerContext()).thenReturn(server);
                when(request.getSession()).thenReturn(session);
                when(session.getId()).thenReturn("localhost:12345");
                when(session.getValue("state")).thenReturn(redisSessionState);
                when(server.getAdminDatabase()).thenReturn(database);
                when(server.isMaster()).thenReturn(true);
                when(server.getValue("state")).thenReturn(redisServerState);

                MockitoAnnotations.initMocks(target);

                command = target.getClass().getAnnotation(CommandUnderTest.class).value().newInstance();

                base.evaluate();

                database.clear();
            }
        };
    }

    public CommandRule withData(String key, DatabaseValue value) {
        database.put(safeKey(key), value);
        return this;
    }

    public CommandRule execute() {
        Mockito.reset(response);
        new RedisCommandWrapper(command).execute(request, response);
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
        Assert.assertThat(database.get(safeKey(key)), matcher);
        return this;
    }

    public CommandRule assertKey(String key, Matcher<DatabaseKey> matcher) {
        Assert.assertThat(database.getKey(safeKey(key)), matcher);
        return this;
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

}
