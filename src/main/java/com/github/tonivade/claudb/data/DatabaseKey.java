/*
 * Copyright (c) 2015-2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.equalizer.Equalizer.equalizer;

import java.io.Serializable;
import java.util.Objects;

import com.github.tonivade.resp.protocol.SafeString;

public class DatabaseKey implements Comparable<DatabaseKey>, Serializable {

  private static final long serialVersionUID = 7710472090270782053L;

  private final SafeString value;

  public DatabaseKey(SafeString value) {
    this.value = value;
  }

  public SafeString getValue() {
    return value;
  }

  @Override
  public int compareTo(DatabaseKey o) {
    return value.compareTo(o.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public boolean equals(Object obj) {
    return equalizer(this)
        .append((one, other) -> Objects.equals(one.value, other.value))
        .applyTo(obj);
  }

  @Override
  public String toString() {
    return value.toString();
  }

  public static DatabaseKey safeKey(SafeString str) {
    return new DatabaseKey(str);
  }

  public static DatabaseKey safeKey(String str) {
    return safeKey(safeString(str));
  }
}
