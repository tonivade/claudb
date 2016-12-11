/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import java.io.IOException;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.SafeString;

import tonivade.db.ITinyDB;
import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;
import tonivade.db.persistence.ByteBufferOutputStream;
import tonivade.db.replication.MasterReplication;

@ReadOnly
@Command("sync")
public class SyncCommand implements ITinyDBCommand {

    private MasterReplication master;

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            ITinyDB server = getTinyDB(request.getServerContext());

            ByteBufferOutputStream output = new ByteBufferOutputStream();
            server.exportRDB(output);

            response.addBulkStr(new SafeString(output.toByteArray()));

            if (master == null) {
                master = new MasterReplication(server);
                master.start();
            }

            master.addSlave(request.getSession().getId());
        } catch (IOException e) {
            response.addError("ERROR replication error");
        }
    }

}
