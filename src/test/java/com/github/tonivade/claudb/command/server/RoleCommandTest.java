/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.server;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.claudb.data.DatabaseValue.set;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(RoleCommand.class)
public class RoleCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);
  
  @Test
  public void executeWithoutSlaves() {
    rule.execute()
        .assertThat(array(string("master"), integer(0), array()));
  }
  
  @Test
  public void executeWithSlaves() {
    rule.withAdminData("slaves", set(safeString("a:1"), safeString("b:2")))
        .execute()
        .assertThat(array(string("master"), integer(0), array(array(string("a"), string("1"), string("0")), 
                                                              array(string("b"), string("2"), string("0")))));
  }
  
  @Test
  public void executeSlave() {
    rule.getServerState().setMaster(false);

    rule.withAdminData("master", hash(entry(safeString("host"), safeString("localhost")),
                                      entry(safeString("port"), safeString("7081")),
                                      entry(safeString("state"), safeString("connected"))))
        .execute()
        .assertThat(array(string("slave"), string("localhost"), integer(7081), string("connected"), integer(0)));
  }
}
