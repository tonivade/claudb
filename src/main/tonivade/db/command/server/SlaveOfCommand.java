/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import tonivade.db.TinyDB;
import tonivade.db.command.IRedisCommand;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;
import tonivade.db.replication.SlaveReplication;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;

@ReadOnly
@Command("slaveof")
@ParamLength(2)
public class SlaveOfCommand implements IRedisCommand {

    private SlaveReplication slave;

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        String host = request.getParam(0).toString();
        String port = request.getParam(1).toString();

        boolean stopCurrent = host.equals("NO") && port.equals("ONE");

        if (slave == null) {
            if (!stopCurrent) {
                startReplication(request, host, port);
            }
        } else {
            slave.stop();

            if (!stopCurrent) {
                startReplication(request, host, port);
            }
        }

        response.addSimpleStr(IResponse.RESULT_OK);
    }

    private void startReplication(IRequest request, String host, String port) {
        slave = new SlaveReplication(
                (TinyDB) request.getServerContext(), request.getSession(), host, Integer.parseInt(port));

        slave.start();
    }

}
