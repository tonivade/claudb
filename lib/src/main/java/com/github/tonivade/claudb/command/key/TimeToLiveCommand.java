/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;

import java.time.Instant;

import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;

public abstract class TimeToLiveCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    DatabaseValue value = db.get(safeKey(request.getParam(0)));
    if (value != null) {
      return keyExists(value);
    } else {
      return notExists();
    }
  }

  protected abstract int timeToLive(DatabaseValue value, Instant now);

  private RedisToken keyExists(DatabaseValue value) {
    if (value.getExpiredAt() != null) {
      return hasExpiredAt(value);
    } else {
      return integer(-1);
    }
  }

  private RedisToken hasExpiredAt(DatabaseValue value) {
    Instant now = Instant.now();
    if (!value.isExpired(now)) {
      return integer(timeToLive(value, now));
    } else {
      return notExists();
    }
  }

  private RedisToken notExists() {
    return integer(-2);
  }
}
