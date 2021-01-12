/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.server;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.lang.Integer.parseInt;

import com.github.tonivade.claudb.DBServerState;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.ImmutableMap;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

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
    ImmutableMap<SafeString, SafeString> hash = adminDatabase.getHash(safeString("master"));
    return array(string("slave"),
                 string(hash.get(safeString("host")).get()),
                 integer(hash.get(safeString("port")).map(port -> parseInt(port.toString())).get()),
                 string(hash.get(safeString("state")).get()), integer(0));
  }

  private RedisToken master(Database adminDatabase) {
    return array(string("master"), integer(0), array(slaves(adminDatabase)));
  }

  private ImmutableList<RedisToken> slaves(Database adminDatabase) {
    DatabaseValue value = adminDatabase.getOrDefault(safeKey("slaves"), DatabaseValue.EMPTY_SET);
    ImmutableList<SafeString> set = value.getSet().asList().sort(SafeString::compareTo);
    return set.map(SafeString::toString)
        .map(slave -> slave.split(":"))
        .map(slave -> array(string(slave[0]), string(slave[1]), string("0"))).asList();
  }
}
