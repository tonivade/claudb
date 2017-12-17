package com.github.tonivade.claudb.event;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

public class CommandEventTest {
  
  @Test
  void commandEvent() {
    CommandEvent event = new CommandEvent(safeString("command"),
                                          safeString("key"), 0);
    
    assertThat(event.getValue(), equalTo(safeString("key")));
    assertThat(event.getChannel(), equalTo("__keyspace__@0__:command"));
  }
}
