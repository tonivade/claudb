/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.server;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;
import com.github.tonivade.claudb.DBServerState;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ReadOnly
@Command("role")
public class RoleCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    DBServerState serverState = getServerState(request.getServerContext());
    Database adminDatabase = getAdminDatabase(request.getServerContext());
    return serverState.isMaster() ? master(adminDatabase) : slave(adminDatabase);
  }

  private RedisToken slave(Database adminDatabase) {
    Map<SafeString, SafeString> hash = adminDatabase.getHash(safeString("master"));
    return array(string("slave"),
                 string(hash.get(safeString("host"))),
                 integer(parseInt(hash.get(safeString("port")).toString())),
                 string(hash.get(safeString("state"))), integer(0));
  }

  private RedisToken master(Database adminDatabase) {
    return array(string("master"), integer(0), array(slaves(adminDatabase)));
  }

  private List<RedisToken> slaves(Database adminDatabase) {
    DatabaseValue value = adminDatabase.getOrDefault(safeKey("slaves"), DatabaseValue.EMPTY_SET);
    Stream<SafeString> set = value.getSet().stream().sorted(SafeString::compareTo);
    return set.map(SafeString::toString)
        .map(slave -> slave.split(":"))
        .map(slave -> array(string(slave[0]), string(slave[1]), string("0")))
        .collect(toList());
  }
}
