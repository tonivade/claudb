/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.SafeString.safeString;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(EvalShaCommand.class)
public class EvalShaCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testNotExistingScript() {
    rule.withParams("notExists", "0")
        .execute()
        .assertThat(error("NOSCRIPT No matching script. Please use EVAL"));
  }

  @Test
  public void testExistingScript() {
    rule.withAdminData("scripts", hash(entry(safeString("test"), safeString("return nil"))))
        .withParams("test", "0")
        .execute()
        .assertThat(nullString());
  }
}
