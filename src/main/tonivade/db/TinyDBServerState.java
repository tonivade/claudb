package tonivade.db;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.redis.protocol.SafeString.safeString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.db.data.SimpleDatabase;
import tonivade.db.persistence.RDBInputStream;
import tonivade.db.persistence.RDBOutputStream;

public class TinyDBServerState {

    private static final int RDB_VERSION = 6;

    private static final DatabaseKey SLAVES_KEY = safeKey(safeString("slaves"));

    private boolean master;
    private final List<IDatabase> databases = new ArrayList<>();
    private final IDatabase admin = new SimpleDatabase();

    public TinyDBServerState(int numDatabases) {
        this.master = true;
        for (int i = 0; i < numDatabases; i++) {
            this.databases.add(new SimpleDatabase());
        }
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public boolean isMaster() {
        return master;
    }

    public IDatabase getAdminDatabase() {
        return admin;
    }

    public IDatabase getDatabase(int id) {
        return databases.get(id);
    }

    public void clear() {
        databases.clear();
    }

    public boolean hasSlaves() {
        DatabaseValue slaves = admin.getOrDefault(SLAVES_KEY, DatabaseValue.EMPTY_SET);
        return !slaves.<Set<String>>getValue().isEmpty();
    }

    public void exportRDB(OutputStream output) throws IOException {
        RDBOutputStream rdb = new RDBOutputStream(output);
        rdb.preamble(RDB_VERSION);
        for (int i = 0; i < databases.size(); i++) {
            IDatabase db = databases.get(i);
            if (!db.isEmpty()) {
                rdb.select(i);
                rdb.dabatase(db);
            }
        }
        rdb.end();
    }

    public void importRDB(InputStream input) throws IOException {
        RDBInputStream rdb = new RDBInputStream(input);

        rdb.parse().forEach((i, db) -> this.databases.set(i, db));
    }
}
