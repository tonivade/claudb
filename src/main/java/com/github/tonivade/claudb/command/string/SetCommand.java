/*
 * Copyright (c) 2015-2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.string;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.purefun.Matcher1.instanceOf;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.purefun.Pattern1;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@Command("set")
@ParamLength(2)
public class SetCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    return com.github.tonivade.purefun.type.Try.of(() -> parse(request))
        .map(params -> onSuccess(db, request, params))
        .recover(this::onFailure)
        .get();
  }

  private RedisToken onSuccess(Database db, Request request, Parameters parameters) {
    DatabaseKey key = safeKey(request.getParam(0));
    DatabaseValue value = parseValue(request, parameters);
    return value.equals(saveValue(db, parameters, key, value)) ? responseOk() : nullString();
  }

  private DatabaseValue parseValue(Request request, Parameters parameters) {
    DatabaseValue value = string(request.getParam(1));
    if (parameters.ttl != null) {
      value = value.expiredAt(Instant.now().plus(parameters.ttl));
    }
    return value;
  }

  private RedisToken onFailure(Throwable e) {
    return Pattern1.<Throwable, RedisToken>build()
        .when(instanceOf(SyntaxException.class))
          .returns(error("syntax error"))
        .when(instanceOf(NumberFormatException.class))
          .returns(error("value is not an integer or out of range"))
        .otherwise()
          .returns(error("error: " + e.getMessage()))
        .apply(e);
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
    if (request.getLength() > 2) {
      for (int i = 2; i < request.getLength(); i++) {
        SafeString option = request.getParam(i);
        if (match("EX", option)) {
          if (parameters.ttl != null) {
            throw new SyntaxException();
          }
          parameters.ttl = parseTtl(request, ++i)
              .map(Duration::ofSeconds)
              .orElseThrow(SyntaxException::new);
        } else if (match("PX", option)) {
          if (parameters.ttl != null) {
            throw new SyntaxException();
          }
          parameters.ttl = parseTtl(request, ++i)
              .map(Duration::ofMillis)
              .orElseThrow(SyntaxException::new);
        } else if (match("NX", option)) {
          if (parameters.ifExists) {
            throw new SyntaxException();
          }
          parameters.ifNotExists = true;
        } else if (match("XX", option)) {
          if (parameters.ifNotExists) {
            throw new SyntaxException();
          }
          parameters.ifExists = true;
        } else {
          throw new SyntaxException();
        }
      }
    }
    return parameters;
  }

  private boolean match(String string, SafeString option) {
    return string.equalsIgnoreCase(option.toString());
  }

  private Option<Integer> parseTtl(Request request, int i) {
    Option<SafeString> ttlOption = request.getOptionalParam(i);
    return ttlOption.map(SafeString::toString).map(Integer::parseInt);
  }

  private static class Parameters {
    private boolean ifExists;
    private boolean ifNotExists;
    private TemporalAmount ttl;
  }

  private static class SyntaxException extends RuntimeException {
    private static final long serialVersionUID = 6960370945568192189L;
  }
}
