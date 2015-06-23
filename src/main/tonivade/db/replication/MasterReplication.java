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

import tonivade.db.command.IServerContext;
import tonivade.db.command.Response;
import tonivade.db.data.DatabaseValue;
import tonivade.db.redis.RedisArray;
import tonivade.db.redis.RedisToken;
import tonivade.db.redis.SafeString;

public class MasterReplication implements Runnable {

    private IServerContext server;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public MasterReplication(IServerContext server) {
        this.server = server;
    }

    public void start() {
        executor.scheduleWithFixedDelay(this, 5, 5, TimeUnit.SECONDS);
    }

    public void addSlave(String id) {
        server.getAdminDatabase().merge("slaves", set(id), (oldValue, newValue) -> {
            List<String> merge = new LinkedList<>();
            merge.addAll(newValue.getValue());
            merge.addAll(oldValue.getValue());
            return set(merge);
        });
    }

    @Override
    public void run() {
        String commands = createCommands();

        for (String slave : getSlaves()) {
            server.publish(slave, commands);
        }
    }

    private Set<String> getSlaves() {
        return server.getAdminDatabase().getOrDefault("slaves", DatabaseValue.EMPTY_SET).getValue();
    }

    private String createCommands() {
        Response response = new Response();
        response.addArray(safeAsList("PING"));
        for (RedisArray array : server.getCommands()) {
            RedisToken currentDB = array.remove(0);
            response.addArray(safeAsList("SELECT", valueOf(currentDB.<Integer>getValue())));
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
