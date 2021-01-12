/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.event;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.github.tonivade.purefun.Equal;
import com.github.tonivade.resp.protocol.SafeString;

public abstract class Event {

  private static final Equal<Event> EQUAL = Equal.<Event>of()
      .comparing(e -> e.command)
      .comparing(e -> e.key)
      .comparing(e -> e.schema);

  private SafeString command;
  private SafeString key;
  private int schema;

  public Event(SafeString command, SafeString key, int schema) {
    this.command = requireNonNull(command);
    this.key = requireNonNull(key);
    this.schema = schema;
  }

  public SafeString getCommand() {
    return command;
  }

  public SafeString getKey() {
    return key;
  }

  public int getSchema() {
    return schema;
  }

  public abstract String getChannel();
  public abstract SafeString getValue();

  @Override
  public boolean equals(Object obj) {
    return EQUAL.applyTo(this, obj);
  }

  @Override
  public int hashCode() {
    return Objects.hash(command, key, schema);
  }

  public static KeyEvent keyEvent(SafeString command, SafeString key, int schema) {
    return new KeyEvent(command, key, schema);
  }

  public static KeySpace commandEvent(SafeString command, SafeString key, int schema) {
    return new KeySpace(command, key, schema);
  }
}
