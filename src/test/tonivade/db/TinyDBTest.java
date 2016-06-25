/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;

import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class TinyDBTest {

    @Rule
    public final TinyDBRule rule = new TinyDBRule();

    @Test
    public void testCommands() {
        try (Jedis jedis = createClientConnection()) {
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
            assertThat(jedis.quit(), equalTo("OK"));
        }
    }

    @Test
    public void testPipeline() {
        try (Jedis jedis = createClientConnection()) {
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
        }
    }

    @Test
    public void testLoad100000() {
        int times = 100000;
        try (Jedis jedis = createClientConnection()) {
            long start = System.nanoTime();
            for (int i = 0; i < times; i++) {
                jedis.set(key(i), value(i));
            }
            jedis.quit();
            assertThat((System.nanoTime() - start) / times, lessThan(1000000L));
        }
    }

    private Jedis createClientConnection() {
        return new Jedis(ITinyDB.DEFAULT_HOST, ITinyDB.DEFAULT_PORT);
    }

    private String value(int i) {
        return "value" + String.valueOf(i);
    }

    private String key(int i) {
        return "key" + String.valueOf(i);
    }

}
