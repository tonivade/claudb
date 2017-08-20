/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseValue.entry;
import static com.github.tonivade.tinydb.data.DatabaseValue.hash;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(ScriptCommands.class)
public class ScriptCommandsTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  private final String sha1sum = "79CEFB99366D8809D2E903C5F36F50C2B731913F";

  @Test
  public void load() {
    rule.withParams("load", "return nil")
        .execute()
        .assertThat(string(sha1sum));

    assertThat(rule.getServerState().getScript(safeString(sha1sum)), equalTo(Optional.of(safeString("return nil"))));
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

    assertThat(rule.getServerState().getScript(safeString(sha1sum)), equalTo(Optional.empty()));
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
