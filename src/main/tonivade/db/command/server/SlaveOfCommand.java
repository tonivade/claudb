/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;
import tonivade.db.replication.SlaveReplication;

@ReadOnly
@Command("slaveof")
@ParamLength(2)
public class SlaveOfCommand implements ITinyDBCommand {

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
                getTinyDB(request.getServerContext()), request.getSession(), host, Integer.parseInt(port));

        slave.start();
    }

}
