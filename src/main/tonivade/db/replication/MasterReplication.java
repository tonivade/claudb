/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import static java.lang.String.valueOf;
import static tonivade.db.data.DatabaseValue.set;
import static tonivade.db.redis.SafeString.safeAsList;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import tonivade.db.command.IServerContext;
import tonivade.db.command.Response;
import tonivade.db.data.DatabaseValue;
import tonivade.db.redis.RedisArray;
import tonivade.db.redis.RedisToken;
import tonivade.db.redis.SafeString;

public class MasterReplication implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(MasterReplication.class.getName());

    private static final String SELECT_COMMAND = "SELECT";
    private static final String PING_COMMAND = "PING";

    private static final String SLAVES_KEY = "slaves";

    private static final int TASK_DELAY = 2;

    private IServerContext server;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public MasterReplication(IServerContext server) {
        this.server = server;
    }

    public void start() {
        executor.scheduleWithFixedDelay(this, TASK_DELAY, TASK_DELAY, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdown();
    }

    public void addSlave(String id) {
        server.getAdminDatabase().merge(SLAVES_KEY, set(id), (oldValue, newValue) -> {
            List<String> merge = new LinkedList<>();
            merge.addAll(oldValue.getValue());
            merge.addAll(newValue.getValue());
            return set(merge);
        });
        LOGGER.info(() -> "new slave: " + id);
    }

    public void removeSlave(String id) {
        server.getAdminDatabase().merge(SLAVES_KEY, set(id), (oldValue, newValue) -> {
            List<String> merge = new LinkedList<>();
            merge.addAll(oldValue.getValue());
            merge.removeAll(newValue.getValue());
            return set(merge);
        });
        LOGGER.info(() -> "slave revomed: " + id);
    }

    @Override
    public void run() {
        String commands = createCommands();

        for (String slave : getSlaves()) {
            server.publish(slave, commands);
        }
    }

    private Set<String> getSlaves() {
        return server.getAdminDatabase().getOrDefault(SLAVES_KEY, DatabaseValue.EMPTY_SET).getValue();
    }

    private String createCommands() {
        Response response = new Response();
        response.addArray(safeAsList(PING_COMMAND));
        for (RedisArray array : server.getCommands()) {
            RedisToken currentDB = array.remove(0);
            response.addArray(safeAsList(SELECT_COMMAND, valueOf(currentDB.<Integer>getValue())));
            response.addArray(toList(array));
        }
        return response.toString();
    }

    private List<SafeString> toList(RedisArray request) {
        List<SafeString> cmd = new LinkedList<>();
        for (RedisToken token : request) {
            cmd.add(token.<SafeString>getValue());
        }
        return cmd;
    }

}
