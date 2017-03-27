package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.command.IServerContext;
import com.github.tonivade.resp.command.ISession;

@RunWith(MockitoJUnitRunner.class)
public class RedisBindingTest
{
  @Mock
  private IRequest request;
  @InjectMocks
  private RedisBinding redis;

  @Mock
  private IServerContext context;
  @Mock
  private ISession session;
  @Mock
  private ICommand command;

  @Test
  public void call()
  {
    when(request.getServerContext()).thenReturn(context);
    when(request.getSession()).thenReturn(session);
    when(context.getCommand("command")).thenReturn(command);

    redis.call(safeString("command"), safeString("param1"), safeString("param2"));

    verify(command).execute(any(IRequest.class), any(IResponse.class));
  }
}
