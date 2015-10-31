/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import static java.lang.String.valueOf;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.set;
import static tonivade.server.protocol.SafeString.safeAsList;
import static tonivade.server.protocol.SafeString.safeString;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import tonivade.db.ITinyDB;
import tonivade.db.RedisServerState;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.server.command.Response;
import tonivade.server.protocol.RedisToken;
import tonivade.server.protocol.SafeString;

public class MasterReplication implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(MasterReplication.class.getName());

    private static final String SELECT_COMMAND = "SELECT";
    private static final String PING_COMMAND = "PING";

    private static final DatabaseKey SLAVES_KEY = safeKey(safeString("slaves"));

    private static final int TASK_DELAY = 2;

    private final ITinyDB server;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public MasterReplication(ITinyDB server) {
        this.server = server;
    }

    public void start() {
        executor.scheduleWithFixedDelay(this, TASK_DELAY, TASK_DELAY, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdown();
    }

    public void addSlave(String id) {
        getAdminDatabase().merge(SLAVES_KEY, set(safeString(id)), (oldValue, newValue) -> {
            List<SafeString> merge = new LinkedList<>();
            merge.addAll(oldValue.getValue());
            merge.addAll(newValue.getValue());
            return set(merge);
        });
        LOGGER.info(() -> "new slave: " + id);
    }

    private IDatabase getAdminDatabase() {
        return getServerState().getAdminDatabase();
    }

    public void removeSlave(String id) {
        getAdminDatabase().merge(SLAVES_KEY, set(safeString(id)), (oldValue, newValue) -> {
            List<SafeString> merge = new LinkedList<>();
            merge.addAll(oldValue.getValue());
            merge.removeAll(newValue.getValue());
            return set(merge);
        });
        LOGGER.info(() -> "slave revomed: " + id);
    }

    @Override
    public void run() {
        String commands = createCommands();

        for (SafeString slave : getSlaves()) {
            server.publish(slave.toString(), commands);
        }
    }

    private Set<SafeString> getSlaves() {
        return getAdminDatabase().getOrDefault(SLAVES_KEY, DatabaseValue.EMPTY_SET).getValue();
    }

    private String createCommands() {
        Response response = new Response();
        response.addArray(safeAsList(PING_COMMAND));
        for (List<RedisToken> array : server.getCommands()) {
            RedisToken currentDB = array.remove(0);
            response.addArray(safeAsList(SELECT_COMMAND, valueOf(currentDB.<Integer>getValue())));
            response.addArray(toList(array));
        }
        return response.toString();
    }

    private List<SafeString> toList(List<RedisToken> request) {
        List<SafeString> cmd = new LinkedList<>();
        for (RedisToken token : request) {
            cmd.add(token.<SafeString>getValue());
        }
        return cmd;
    }

    private RedisServerState getServerState() {
        return server.getValue("state");
    }

}
