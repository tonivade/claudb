/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.key;

import java.time.Instant;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.claudb.data.DatabaseValue;

@Command("ttl")
@ParamLength(1)
public class TimeToLiveSecondsCommand extends TimeToLiveCommand {

  @Override
  protected int timeToLive(DatabaseValue value, Instant now) {
    return value.timeToLiveSeconds(now);
  }
}
