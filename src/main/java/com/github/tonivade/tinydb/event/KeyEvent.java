package com.github.tonivade.tinydb.event;

import static java.lang.String.format;

import com.github.tonivade.resp.protocol.SafeString;

class KeyEvent extends Event {
  
  private static final String KEYEVENT = "__keyevent__@%d__:%s";

  public KeyEvent(SafeString command, SafeString key, int schema) {
    super(command, key, schema);
  }
  
  @Override
  SafeString getValue() {
    return getCommand();
  }
  
  @Override
  String getChannel() {
    return format(KEYEVENT, getSchema(), getKey());
  }
}
