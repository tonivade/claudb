/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import static org.hamcrest.CoreMatchers.is;
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
    public void testCommands() throws Exception {
        try (Jedis jedis = new Jedis(ITinyDB.DEFAULT_HOST, ITinyDB.DEFAULT_PORT)) {
            assertThat(jedis.ping(), is("PONG"));
            assertThat(jedis.echo("Hi!"), is("Hi!"));
            assertThat(jedis.set("a", "1"), is("OK"));
            assertThat(jedis.strlen("a"), is(1L));
            assertThat(jedis.strlen("b"), is(0L));
            assertThat(jedis.exists("a"), is(true));
            assertThat(jedis.exists("b"), is(false));
            assertThat(jedis.get("a"), is("1"));
            assertThat(jedis.get("b"), is(nullValue()));
            assertThat(jedis.getSet("a", "2"), is("1"));
            assertThat(jedis.get("a"), is("2"));
            assertThat(jedis.del("a"), is(1L));
            assertThat(jedis.get("a"), is(nullValue()));
            assertThat(jedis.quit(), is("OK"));
        }
    }

    @Test
    public void testPipeline() throws Exception {
        try (Jedis jedis = new Jedis(ITinyDB.DEFAULT_HOST, ITinyDB.DEFAULT_PORT)) {
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
            assertThat(result.next(), is("PONG"));
            assertThat(result.next(), is("Hi!"));
            assertThat(result.next(), is("OK"));
            assertThat(result.next(), is(1L));
            assertThat(result.next(), is(0L));
            assertThat(result.next(), is(true));
            assertThat(result.next(), is(false));
            assertThat(result.next(), is("1"));
            assertThat(result.next(), is(nullValue()));
            assertThat(result.next(), is("1"));
            assertThat(result.next(), is("2"));
            assertThat(result.next(), is(1L));
            assertThat(result.next(), is(nullValue()));

            jedis.quit();
        }
    }

    @Test
    public void testLoad1000() throws Exception {
        loadTest(1000);
    }

    @Test
    public void testLoad10000() throws Exception {
        loadTest(10000);
    }

    @Test
    public void testLoad100000() throws Exception {
        loadTest(100000);
    }

    private void loadTest(int times) {
        long start = System.nanoTime();
        try (Jedis jedis = new Jedis(ITinyDB.DEFAULT_HOST, ITinyDB.DEFAULT_PORT)) {
            for (int i = 0; i < times; i++) {
                jedis.set(key(i), value(i));
            }
            jedis.quit();
        }
        assertThat((System.nanoTime() - start) / times, is(lessThan(1000000L)));
    }

    private String value(int i) {
        return "value" + String.valueOf(i);
    }

    private String key(int i) {
        return "key" + String.valueOf(i);
    }

}
