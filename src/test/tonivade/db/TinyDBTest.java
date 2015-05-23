package tonivade.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class TinyDBTest {

    private TinyDB db = new TinyDB();

    private Thread server = new Thread(() -> {
        db.init();
        db.start();
    });

    @Before
    public void setUp() throws Exception {
        server.start();
        Thread.sleep(1000);
    }

    @After
    public void tearDown() throws Exception {
        db.stop();
    }

    @Test
    public void testCommands() throws Exception {
        try (Jedis jedis = new Jedis("localhost", 7081)) {
            assertThat(jedis.ping(), is("PONG"));
            assertThat(jedis.set("a", "1"), is("OK"));
            assertThat(jedis.get("a"), is("1"));
            assertThat(jedis.get("b"), is(nullValue()));
            assertThat(jedis.del("a"), is(1L));
            assertThat(jedis.get("a"), is(nullValue()));
        }
    }

    @Test(timeout=1000)
    public void testLoad1000() throws Exception {
        loadTest(1000);
    }

    @Test(timeout=2000)
    public void testLoad10000() throws Exception {
        loadTest(10000);
    }

    @Test(timeout=20000)
    public void testLoad100000() throws Exception {
        loadTest(100000);
    }

    private void loadTest(int times) {
        try (Jedis jedis = new Jedis("localhost", 7081)) {
            for (int i = 0; i < times; i++) {
                jedis.set(key(i), value(i));
            }
        }
    }

    private String value(int i) {
        return "value" + String.valueOf(i);
    }

    private String key(int i) {
        return "key" + String.valueOf(i);
    }

}
