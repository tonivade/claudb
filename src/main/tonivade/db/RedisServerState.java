package tonivade.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import tonivade.db.data.Database;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.db.persistence.RDBInputStream;
import tonivade.db.persistence.RDBOutputStream;

public class RedisServerState {

    private static final String SLAVES_KEY = "slaves";

    private boolean master;
    private final List<IDatabase> databases = new ArrayList<>();
    private final IDatabase admin = new Database();

    public RedisServerState(int numDatabases) {
        this.master = true;
        for (int i = 0; i < numDatabases; i++) {
            this.databases.add(new Database());
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
        return !admin.getOrDefault(SLAVES_KEY, DatabaseValue.EMPTY_SET).<Set<String>>getValue().isEmpty();
    }

    public void exportRDB(OutputStream output) throws IOException {
        RDBOutputStream rdb = new RDBOutputStream(output);
        rdb.preamble(6);
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

        for (Entry<Integer, IDatabase> entry : rdb.parse().entrySet()) {
            this.databases.set(entry.getKey(), entry.getValue());
        }
    }

}
