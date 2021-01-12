/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.string;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.status;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.claudb.data.DatabaseValue;

@CommandUnderTest(SetCommand.class)
public class SetCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withParams("a", "1")
        .execute()
        .assertValue("a", is(string("1")))
        .assertThat(status("OK"));
  }

  @Test
  public void testExecuteEx() {
    rule.withParams("a", "1", "EX", "10")
        .execute()
        .assertValue("a", is(string("1")))
        .assertThat(status("OK"));
  }

  @Test
  public void testExecutePx() {
    rule.withParams("a", "1", "PX", "10")
        .execute()
        .assertValue("a", is(string("1")))
        .assertThat(status("OK"));
  }

  @Test
  public void testExecuteNxIfNotExists() {
    rule.withParams("a", "1", "NX")
        .execute()
        .assertValue("a", is(string("1")))
        .assertThat(status("OK"));
  }

  @Test
  public void testExecuteNxIfExists() {
    rule.withData("a", string("1"))
        .withParams("a", "2", "NX")
        .execute()
        .assertValue("a", is(string("1")))
        .assertThat(nullString());
  }

  @Test
  public void testExecuteXXIfExists() {
    rule.withData("a", string("1"))
        .withParams("a", "2", "XX")
        .execute()
        .assertValue("a", is(string("2")))
        .assertThat(status("OK"));
  }

  @Test
  public void testExecuteXXIfNotExists() {
    rule.withParams("a", "2", "XX")
        .execute()
        .assertValue("a", is(DatabaseValue.NULL))
        .assertThat(nullString());
  }

  @Test
  public void testExecuteSyntaxErrorEx() {
    rule.withParams("a", "2", "EX")
        .execute()
        .assertThat(error("syntax error"));
  }

  @Test
  public void testExecuteSyntaxErrorExFormat() {
    rule.withParams("a", "2", "EX", "a")
        .execute()
        .assertThat(error("value is not an integer or out of range"));
  }

  @Test
  public void testExecuteSyntaxErrorPx() {
    rule.withParams("a", "2", "PX")
        .execute()
        .assertThat(error("syntax error"));
  }

  @Test
  public void testExecuteSyntaxErrorPxFormat() {
    rule.withParams("a", "2", "PX", "a")
        .execute()
        .assertThat(error("value is not an integer or out of range"));
  }

  @Test
  public void testExecuteSyntaxErrorExPx() {
    rule.withParams("a", "2", "EX", "10", "PX", "11")
        .execute()
        .assertThat(error("syntax error"));
  }

  @Test
  public void testExecuteSyntaxErrorPxEx() {
    rule.withParams("a", "2", "PX", "10", "EX", "11")
        .execute()
        .assertThat(error("syntax error"));
  }

  @Test
  public void testExecuteSyntaxErrorNxXx() {
    rule.withParams("a", "2", "NX", "XX")
        .execute()
        .assertThat(error("syntax error"));
  }

  @Test
  public void testExecuteSyntaxErrorXxNx() {
    rule.withParams("a", "2", "XX", "NX")
        .execute()
        .assertThat(error("syntax error"));
  }

  @Test
  public void testExecuteSyntaxError() {
    rule.withParams("a", "2", "ZX")
        .execute()
        .assertThat(error("syntax error"));
  }
}
