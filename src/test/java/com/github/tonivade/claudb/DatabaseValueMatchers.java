/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb;

import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.resp.protocol.SafeString.safeAsList;
import static com.github.tonivade.resp.protocol.SafeString.safeString;

import java.time.Instant;
import java.util.Map.Entry;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;

import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.resp.protocol.SafeString;

public class DatabaseValueMatchers {

  public static DatabaseValue list(String ... strings) {
    return DatabaseValue.list(safeAsList(strings));
  }

  public static DatabaseValue set(String ... strings) {
    return DatabaseValue.set(safeAsList(strings));
  }

  public static Tuple2<SafeString, SafeString> entry(String key, String value) {
    return DatabaseValue.entry(safeString(key), safeString(value));
  }

  public static Entry<Double, SafeString> score(double score, String value) {
    return DatabaseValue.score(score, safeString(value));
  }

  public static Matcher<DatabaseValue> isString(String expected) {
    return IsEqual.equalTo(string(expected));
  }

  public static Matcher<DatabaseValue> isList(String ... expected) {
    return IsEqual.equalTo(list(expected));
  }

  public static Matcher<DatabaseValue> isSet(String ... expected) {
    return IsEqual.equalTo(set(expected));
  }

  public static Matcher<DatabaseValue> notNullValue() {
    return IsNull.notNullValue(DatabaseValue.class);
  }

  public static Matcher<DatabaseValue> nullValue() {
    return IsNull.nullValue(DatabaseValue.class);
  }

  public static Matcher<DatabaseValue> isExpired() {
    return new ValueExpiredMatcher();
  }

  public static Matcher<DatabaseValue> isNotExpired() {
    return new ValueNotExpiredMatcher();
  }

  private static class ValueExpiredMatcher extends TypeSafeMatcher<DatabaseValue> {

    @Override
    public void describeTo(org.hamcrest.Description description) {
      description.appendText("Value is expired");
    }

    @Override
    protected boolean matchesSafely(DatabaseValue item) {
      return item.isExpired(Instant.now());
    }
  }

  private static class ValueNotExpiredMatcher extends TypeSafeMatcher<DatabaseValue> {

    @Override
    public void describeTo(org.hamcrest.Description description) {
      description.appendText("Value is not expired");
    }

    @Override
    protected boolean matchesSafely(DatabaseValue item) {
      return !item.isExpired(Instant.now());
    }
  }

}
