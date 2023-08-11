/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.util.Iterator;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import com.github.tonivade.claudb.DBConfig.Engine;
import com.github.tonivade.claudb.junit5.ClauDBTest;
import com.github.tonivade.resp.RespServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@ClauDBTest
class ClauDBServerTest {

  static RespServer server = ClauDB.builder().randomPort().withEngine(Engine.LUAJ).build();

  @Test
  void testCommands() {
    execute(jedis -> {
      assertThat(jedis.ping(), equalTo("PONG"));
      assertThat(jedis.echo("Hi!"), equalTo("Hi!"));
      assertThat(jedis.set("a", "1"), equalTo("OK"));
      assertThat(jedis.strlen("a"), equalTo(1L));
      assertThat(jedis.strlen("b"), equalTo(0L));
      assertThat(jedis.exists("a"), equalTo(true));
      assertThat(jedis.exists("b"), equalTo(false));
      assertThat(jedis.get("a"), equalTo("1"));
      assertThat(jedis.get("b"), nullValue());
      assertThat(jedis.getSet("a", "2"), equalTo("1"));
      assertThat(jedis.get("a"), equalTo("2"));
      assertThat(jedis.del("a"), equalTo(1L));
      assertThat(jedis.get("a"), nullValue());
      assertThat(jedis.eval("return 1"), equalTo(1L));
      assertThat(jedis.quit(), equalTo("OK"));
    });
  }

  @Test
  void testPipeline() {
    execute(jedis -> {
      Pipeline p = jedis.pipelined();
      p.ping();
      p.echo("Hi!");
      p.set("a", "1");
      p.strlen("a");
      p.strlen("b");
      p.exists("a");
      p.exists("b");
      p.get("a");
      p.get("b");
      p.getSet("a", "2");
      p.get("a");
      p.del("a");
      p.get("a");

      Iterator<Object> result = p.syncAndReturnAll().iterator();
      assertThat(result.next(), equalTo("PONG"));
      assertThat(result.next(), equalTo("Hi!"));
      assertThat(result.next(), equalTo("OK"));
      assertThat(result.next(), equalTo(1L));
      assertThat(result.next(), equalTo(0L));
      assertThat(result.next(), equalTo(true));
      assertThat(result.next(), equalTo(false));
      assertThat(result.next(), equalTo("1"));
      assertThat(result.next(), nullValue());
      assertThat(result.next(), equalTo("1"));
      assertThat(result.next(), equalTo("2"));
      assertThat(result.next(), equalTo(1L));
      assertThat(result.next(), nullValue());

      jedis.quit();
    });
  }

  @Test
  void testEval() {
    execute(jedis -> assertThat(jedis.eval("return 1"), equalTo(1L)));
  }

  @Test
  void testEvalScript() {
    String script = "local keys = redis.call('keys', '*region*') "
        + "for i,k in ipairs(keys) do local res = redis.call('del', k) end";
    execute(jedis -> assertThat(jedis.eval(script), nullValue()));
  }

  @Test
  void testBugNotExist() {
    String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

    execute(jedis -> {
      jedis.del("key");
      Object eval = jedis.eval(script, singletonList("key"), singletonList("value"));
      assertThat(eval, equalTo(0L));
    });
  }

  @Test
  void testNotExist() {
    String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

    execute(jedis -> {
      jedis.set("key", "value");
      Object eval = jedis.eval(script, singletonList("key"), singletonList("value"));
      assertThat(eval, equalTo(1L));
      assertThat(jedis.exists("key"), is(false));
    });
  }

  @Test
  public void testPushPull() {
    execute(jedis -> {
      long push = jedis.rpush("key", "val1", "val2");
      String pop1 = jedis.rpop("key");
      String pop2 = jedis.rpop("key");
      assertThat(push, is(2l));
      assertThat(pop1, equalTo("val2"));
      assertThat(pop2, equalTo("val1"));
    });
  }

  private void execute(Consumer<Jedis> action) {
    try (Jedis jedis = createClientConnection()) {
      action.accept(jedis);
    }
  }

  private Jedis createClientConnection() {
    return new Jedis(DBServerContext.DEFAULT_HOST, server.getPort(), 10000);
  }
}
