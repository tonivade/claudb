/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.key;

import java.time.Instant;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.claudb.data.DatabaseValue;

@Command("pttl")
@ParamLength(1)
public class TimeToLiveMillisCommand extends TimeToLiveCommand {

  @Override
  protected int timeToLive(DatabaseValue value, Instant now) {
    return (int) value.timeToLiveMillis(now);
  }
}
