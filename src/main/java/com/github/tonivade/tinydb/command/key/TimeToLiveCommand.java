/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.key;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import java.time.Instant;

import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;

public abstract class TimeToLiveCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    DatabaseKey key = db.getKey(safeKey(request.getParam(0)));
    if (key != null) {
      return keyExists(key);
    } else {
      return notExists();
    }
  }

  protected abstract int timeToLive(DatabaseKey key, Instant now);

  private RedisToken keyExists(DatabaseKey key) {
    if (key.expiredAt() != null) {
      return hasExpiredAt(key);
    } else {
      return RedisToken.integer(-1);
    }
  }

  private RedisToken hasExpiredAt(DatabaseKey key) {
    Instant now = Instant.now();
    if (!key.isExpired(now)) {
      return RedisToken.integer(timeToLive(key, now));
    } else {
      return notExists();
    }
  }

  private RedisToken notExists() {
    return RedisToken.integer(-2);
  }
}
