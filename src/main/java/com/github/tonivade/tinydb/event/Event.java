package com.github.tonivade.tinydb.event;

import static java.util.Objects.requireNonNull;
import static tonivade.equalizer.Equalizer.equalizer;

import java.util.Objects;

import com.github.tonivade.resp.protocol.SafeString;

public abstract class Event
{
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
  
  public boolean applyTo(String pattern) {
    // TODO:
    return true;
  }
  
  public abstract SafeString toChannel();
  public abstract SafeString getValue();
  
  @Override
  public boolean equals(Object obj) {
    return equalizer(this)
         .append((o1, o2) -> Objects.equals(o1.command, o2.command))
         .append((o1, o2) -> Objects.equals(o1.key, o2.key))
         .append((o1, o2) -> Objects.equals(o1.schema, o2.schema))
         .applyTo(obj);
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hash(command, key, schema);
  }
}
