/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.status;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(EvalCommand.class)
public class EvalCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecuteVoid() {
    rule.withParams("return nil", "0")
        .execute()
        .assertThat(nullString());
  }

  @Test
  public void testExecuteInteger() {
    rule.withParams("return 1", "0")
        .execute()
        .assertThat(integer(1));
  }

  @Test
  public void testExecuteBoolean() {
    rule.withParams("return true", "0")
        .execute()
        .assertThat(integer(1));
  }

  @Test
  public void testExecuteString() {
    rule.withParams("return 'hello'", "0")
        .execute()
        .assertThat(string("hello"));
  }

  @Test
  public void testExecuteArray() {
    rule.withParams("return {'hello', 'world'}", "0")
        .execute()
        .assertThat(array(string("hello"), string("world")));
  }

  @Test
  public void testExecuteKeys() {
    rule.withParams("return {KEYS[1], KEYS[2]}", "2", "hello", "world")
        .execute()
        .assertThat(array(string("hello"), string("world")));
  }

  @Test
  public void testExecuteArguments() {
    rule.withParams("return {ARGV[1], ARGV[2]}", "0", "hello", "world")
        .execute()
        .assertThat(array(string("hello"), string("world")));
  }

  @Test
  public void testExecuteKeysAndArguments() {
    rule.withParams("return {KEYS[1], KEYS[2], ARGV[1], ARGV[2]}", "2", "hello", "world", "or", "not")
        .execute()
        .assertThat(array(string("hello"), string("world"), string("or"), string("not")));
  }

  @Test
  public void testExecuteKeysError() {
    rule.withParams("return {KEYS[1], KEYS[2]}", "2", "hello")
        .execute()
        .assertThat(error("invalid number of arguments"));
  }

  @Test
  public void testExecuteCommand() {
    rule.withCommand("ping", request -> status("PONG"))
        .withParams("return redis.call('ping')", "0")
        .execute()
        .assertThat(string("PONG"));
  }

  @Test
  public void testExecuteCommandWithParam() {
    rule.withCommand("echo", request -> string(request.getParam(0)))
        .withParams("return redis.call('echo', 'hello world!')", "0")
        .execute()
        .assertThat(string("hello world!"));
  }

  @Test
  public void testExecuteScriptError() {
    rule.withParams("return '1", "0")
        .execute()
        .assertThat(error("eval threw javax.script.ScriptException: [string \"script\"]:1: unfinished string"));
  }

  @Test
  public void testExecuteScript() {
    rule.withCommand("keys", request -> array(string("key1"), string("value1"), string("key2"), string("value2")))
        .withCommand("del", request -> integer(true))
        .withParams("local keys = redis.call('keys', '*region*') for i,k in ipairs(keys) do local res = redis.call('del', k) end", "0")
        .execute()
        .assertThat(nullString());
  }
}
