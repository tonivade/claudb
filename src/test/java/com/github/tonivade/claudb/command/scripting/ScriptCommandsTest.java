/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.purefun.type.Option;

@CommandUnderTest(ScriptCommands.class)
public class ScriptCommandsTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  private final String sha1sum = "79cefb99366d8809d2e903c5f36f50c2b731913f";

  @Test
  public void load() {
    rule.withParams("LOAD", "return nil")
        .execute()
        .assertThat(string(sha1sum));

    assertThat(rule.getServerState().getScript(safeString(sha1sum)), equalTo(Option.some(safeString("return nil"))));
  }

  @Test
  public void exists() {
    rule.withAdminData("scripts", hash(entry(safeString(sha1sum), safeString("return nil"))))
        .withParams("exists", sha1sum)
        .execute()
        .assertThat(integer(true));
  }

  @Test
  public void flush() {
    rule.withAdminData("scripts", hash(entry(safeString(sha1sum), safeString("return nil"))))
        .withParams("flush")
        .execute()
        .assertThat(responseOk());

    assertThat(rule.getServerState().getScript(safeString(sha1sum)), equalTo(Option.none()));
  }

  @Test
  public void notExists() {
    rule.withParams("exists", sha1sum)
        .execute()
        .assertThat(integer(false));
  }

  @Test
  public void unknown() {
    rule.withParams("asdf", sha1sum)
        .execute()
        .assertThat(error("Unknown SCRIPT subcommand: asdf"));
  }
}
