/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.event;

import static java.lang.String.format;

import com.github.tonivade.resp.protocol.SafeString;

class KeySpace extends Event {
  
  private static final String CHANNEL_PATTERN = "__keyspace__@%d__:%s";

  public KeySpace(SafeString command, SafeString key, int schema) {
    super(command, key, schema);
  }
  
  @Override
  public SafeString getValue() {
    return getKey();
  }
  
  @Override
  public String getChannel() {
    return format(CHANNEL_PATTERN, getSchema(), getCommand());
  }
}
