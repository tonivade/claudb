/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.IServerContext;
import tonivade.db.command.annotation.Command;
import tonivade.db.data.IDatabase;
import tonivade.db.replication.MasterReplication;

@Command("sync")
public class SyncCommand implements ICommand {

    private MasterReplication master;

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            IServerContext server = request.getServerContext();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            server.exportRDB(baos);

            response.addBulkStr(baos.toString("UTF-8"));

            if (master == null) {
                master = new MasterReplication(server);
                master.start();
            }

            master.addSlave(request.getSession().getId());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
