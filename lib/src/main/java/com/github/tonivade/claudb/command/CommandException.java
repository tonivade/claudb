package com.github.tonivade.claudb.command;

import com.github.tonivade.resp.protocol.AbstractRedisToken.ErrorRedisToken;

/**
 * Root exception for all problems which happens during command execution.
 * If happens it will be automatically converted into {@link ErrorRedisToken} and send in response.
 */
public class CommandException extends RuntimeException {
  public CommandException(String message) {
    super(message);
  }

  public CommandException(String message, Throwable cause) {
    super(message, cause);
  }

  public CommandException(Throwable cause) {
    super(cause);
  }
}
