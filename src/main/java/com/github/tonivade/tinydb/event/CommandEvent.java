package com.github.tonivade.tinydb.event;

import static java.lang.String.format;

import com.github.tonivade.resp.protocol.SafeString;

public class CommandEvent extends Event {
  
  private static final String KEYSPACE = "__keyspace__@%d__:%s";

  public CommandEvent(SafeString command, SafeString key, int schema) {
    super(command, key, schema);
  }
  
  @Override
  public SafeString getValue() {
    return getCommand();
  }
  
  @Override
  public String getChannel() {
    return format(KEYSPACE, getSchema(), getCommand());
  }
}
