package tonivade.db;

import redis.clients.jedis.Jedis;


public class SimpleTest extends Thread{

    private TinyDB db = new TinyDB();

    @Override
    public void run() {
        db.init();
        db.start();
    }

    public void kill() {
        db.stop();
    }

    public static void main(String[] args) throws Exception {
        SimpleTest test = new SimpleTest();
        test.start();

        Thread.sleep(3000);

        Jedis jedis = new Jedis("localhost", 7081);
//        Jedis jedis = new Jedis("localhost", 6379);
        long nanos = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            jedis.set("key" + String.valueOf(i), "value" + String.valueOf(i));
        }
        System.out.println(System.currentTimeMillis() - nanos);
        jedis.close();
        test.kill();
    }

}
