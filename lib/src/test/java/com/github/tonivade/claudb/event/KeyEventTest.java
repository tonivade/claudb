/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.event;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class KeyEventTest {

  @Test
  void keyEvent() {
    KeyEvent event = new KeyEvent(safeString("command"), safeString("key"), 0);
    
    assertThat(event.getValue(), equalTo(safeString("command")));
    assertThat(event.getChannel(), equalTo("__keyevent__@0__:key"));
  }
}
