/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.server;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static java.lang.Integer.parseInt;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerState;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseValue;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

@ReadOnly
@Command("role")
public class RoleCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    TinyDBServerState serverState = getServerState(request.getServerContext());
    Database adminDatabase = getAdminDatabase(request.getServerContext());
    return serverState.isMaster() ? master(adminDatabase) : slave(adminDatabase);
  }

  private RedisToken slave(Database adminDatabase) {
    Map<SafeString, SafeString> hash = adminDatabase.getHash(safeString("master"));
    return array(string("slave"), 
                 string(hash.get(safeString("host")).get()), 
                 integer(hash.get(safeString("port")).map(port -> parseInt(port.toString())).get()), 
                 string(hash.get(safeString("state")).get()), integer(0));
  }

  private RedisToken master(Database adminDatabase) {
    return array(string("master"), integer(0), array(slaves(adminDatabase).toJavaList()));
  }

  private List<RedisToken> slaves(Database adminDatabase) {
    DatabaseValue value = adminDatabase.getOrDefault(safeKey("slaves"), DatabaseValue.EMPTY_SET);
    Set<SafeString> set = value.getValue();
    return set.map(SafeString::toString)
        .map(slave -> slave.split(":"))
        .map(slave -> array(string(slave[0]), string(slave[1]), string("0"))).toList();
  }
}
