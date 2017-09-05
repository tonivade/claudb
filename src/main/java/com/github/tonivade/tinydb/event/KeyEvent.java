package com.github.tonivade.tinydb.event;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.lang.String.format;

import com.github.tonivade.resp.protocol.SafeString;

public class KeyEvent extends Event {
  
  public KeyEvent(SafeString command, SafeString key, int schema)
  {
    super(command, key, schema);
  }
  
  @Override
  public SafeString getValue() {
    return getCommand();
  }
  
  @Override
  public SafeString toChannel() {
    return safeString(format("__keyevent__@%d__:%s", getSchema(), getKey()));
  }
}
