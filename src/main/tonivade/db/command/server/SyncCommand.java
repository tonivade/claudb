/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import java.io.IOException;

import tonivade.db.ITinyDB;
import tonivade.db.command.IRedisCommand;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;
import tonivade.db.persistence.ByteBufferOutputStream;
import tonivade.db.replication.MasterReplication;
import tonivade.redis.annotation.Command;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.protocol.SafeString;

@ReadOnly
@Command("sync")
public class SyncCommand implements IRedisCommand {

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
