/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(ScriptLoadCommand.class)
public class ScriptLoadCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    String sha1sum = "79CEFB99366D8809D2E903C5F36F50C2B731913F";

    rule.withParams("return nil")
        .execute()
        .assertThat(string(sha1sum));

    assertThat(rule.getServerState().getScript(safeString(sha1sum)), equalTo(Optional.of(safeString("return nil"))));
  }
}
