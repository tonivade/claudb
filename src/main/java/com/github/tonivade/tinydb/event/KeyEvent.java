package com.github.tonivade.tinydb.event;

import static java.lang.String.format;

import com.github.tonivade.resp.protocol.SafeString;

public class KeyEvent extends Event {
  
  private static final String KEYEVENT = "__keyevent__@%d__:%s";

  public KeyEvent(SafeString command, SafeString key, int schema) {
    super(command, key, schema);
  }
  
  @Override
  public SafeString getValue() {
    return getCommand();
  }
  
  @Override
  public String getChannel() {
    return format(KEYEVENT, getSchema(), getKey());
  }
}
