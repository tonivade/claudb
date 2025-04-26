/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.event;

import static com.github.tonivade.resp.util.Precondition.checkNonNull;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.resp.util.Equal;
import java.util.Objects;

public abstract class Event {

  private static final Equal<Event> EQUAL = Equal.<Event>of()
      .comparing(e -> e.command)
      .comparing(e -> e.key)
      .comparing(e -> e.schema);

  private final SafeString command;
  private final SafeString key;
  private final int schema;

  protected Event(SafeString command, SafeString key, int schema) {
    this.command = checkNonNull(command);
    this.key = checkNonNull(key);
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
