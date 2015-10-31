package tonivade.db;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tonivade.redis.protocol.SafeString;

public class RedisSessionState {

    private int db;

    private final Set<SafeString> subscriptions = new HashSet<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public int getCurrentDB() {
        return db;
    }

    public void setCurrentDB(int db) {
        this.db = db;
    }

    public Set<SafeString> getSubscriptions() {
        return subscriptions;
    }

    public void addSubscription(SafeString channel) {
        subscriptions.add(channel);
    }

    public void removeSubscription(SafeString channel) {
        subscriptions.remove(channel);
    }

    public void enqueue(Runnable task) {
        executor.submit(task);
    }

}
