/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

@Command("set")
@ParamLength(2)
public class SetCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    Parameters parameters = parse(request);
    if (parameters.parsetInt) {
      return error("value is not an integer or out of range");
    }
    if (parameters.syntaxError) {
      return error("syntax error");
    }
    if (parameters.ifExists && parameters.ifNotExists) {
      return error("syntax error");
    }
    DatabaseKey key = safeKey(request.getParam(0));
    DatabaseValue value = string(request.getParam(1));
    if (parameters.ttl != null) {
      value = value.expiredAt(Instant.now().plus(parameters.ttl));
    }
    return value.equals(saveValue(db, parameters, key, value)) ? responseOk() : nullString();
  }

  private DatabaseValue saveValue(Database db, Parameters params, DatabaseKey key, DatabaseValue value) {
    DatabaseValue savedValue = null; 
    if (params.ifExists) {
      savedValue = putValueIfExists(db, key, value);
    } else if (params.ifNotExists) {
      savedValue = putValueIfNotExists(db, key, value);
    } else {
      savedValue = putValue(db, key, value);
    }
    return savedValue;
  }

  private DatabaseValue putValue(Database db, DatabaseKey key, DatabaseValue value) {
    db.put(key, value);
    return value;
  }

  private DatabaseValue putValueIfExists(Database db, DatabaseKey key, DatabaseValue value) {
    DatabaseValue oldValue = db.get(key);
    if (oldValue != null) {
      return putValue(db, key, value);
    }
    return oldValue;
  }

  private DatabaseValue putValueIfNotExists(Database db, DatabaseKey key, DatabaseValue value) {
    return db.merge(key, value, (oldValue, newValue) -> oldValue);
  }
    
  private Parameters parse(Request request) {
    Parameters parameters = new Parameters();
    try {
      if (request.getLength() > 2) {
        for (int i = 2; i < request.getLength(); i++) {
          SafeString option = request.getParam(i);
          if ("EX".equalsIgnoreCase(option.toString())) {
            if (request.getLength() > i + 1) {
              SafeString ttlInSeconds = request.getParam(++i);
              parameters.ttl = Duration.ofSeconds(Integer.parseInt(ttlInSeconds.toString()));
            } else {
              parameters.syntaxError = true;
              break;
            }
          } else if ("PX".equalsIgnoreCase(option.toString())) {
            if (request.getLength() > i + 1) {
              SafeString ttlInMillis = request.getParam(++i);
              parameters.ttl = Duration.ofMillis(Integer.parseInt(ttlInMillis.toString()));
            } else {
              parameters.syntaxError = true;
              break;
            }
          } else if ("NX".equalsIgnoreCase(option.toString())) {
            parameters.ifNotExists = true;
          } else if ("XX".equalsIgnoreCase(option.toString())) {
            parameters.ifExists = true;
          } else {
            parameters.syntaxError = true;
            break;
          }
        }
      }
    } catch (NumberFormatException e) {
      parameters.parsetInt = true;
    }
    return parameters;
  }

  private static class Parameters {
    boolean ifExists;
    boolean ifNotExists;
    TemporalAmount ttl;
    boolean syntaxError;
    boolean parsetInt;
  }
}
