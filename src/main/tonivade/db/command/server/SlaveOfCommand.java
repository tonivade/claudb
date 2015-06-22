/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.IDatabase;
import tonivade.db.replication.SlaveReplication;

@Command("slaveof")
@ParamLength(2)
public class SlaveOfCommand implements ICommand {

    private SlaveReplication slave;

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        String host = request.getParam(0);
        String port = request.getParam(1);

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

        response.addSimpleStr(RESULT_OK);
    }

    private void startReplication(IRequest request, String host, String port) {
        slave = new SlaveReplication(
                request.getServerContext(), request.getSession(), host, Integer.parseInt(port));

        slave.start();
    }

}
