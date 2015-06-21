/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import static tonivade.db.data.DatabaseValue.set;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tonivade.db.command.IRequest;
import tonivade.db.command.IServerContext;
import tonivade.db.command.Response;

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
        server.getDatabase().merge("slaves", set(id), (oldValue, newValue) -> {
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
        return server.getDatabase().getOrDefault("slaves", set()).getValue();
    }

    private String createCommands() {
        Response response = new Response();
        response.addArray(Arrays.asList("PING"));
        for (IRequest request : server.getCommands()) {
            response.addArray(toList(request));
        }
        return response.toString();
    }

    private List<String> toList(IRequest request) {
        List<String> cmd = new LinkedList<>();
        cmd.add(request.getCommand());
        cmd.addAll(request.getParams());
        return cmd;
    }

}
