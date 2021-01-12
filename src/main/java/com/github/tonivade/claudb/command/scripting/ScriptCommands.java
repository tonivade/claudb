/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.SafeString.safeString;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.github.tonivade.claudb.DBServerState;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.purefun.Matcher1;
import com.github.tonivade.purefun.Pattern1;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@ParamLength(1)
@Command("script")
public class ScriptCommands implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    return Pattern1.<Request, RedisToken>build()
        .when(isCommand("load"))
          .then(this::load)
        .when(isCommand("exists"))
          .then(this::exists)
        .when(isCommand("flush"))
          .then(this::flush)
        .otherwise()
          .then(this::unknownCommand)
        .apply(request);
  }

  private RedisToken unknownCommand(Request request) {
    return RedisToken.error("Unknown SCRIPT subcommand: " + request.getParam(0));
  }

  private RedisToken load(Request request) {
    SafeString script = request.getParam(1);
    return Try.of(() -> digest(script)).map(sha1 -> {
      DBServerState server = getServerState(request.getServerContext());
      server.saveScript(safeString(sha1), script);
      return RedisToken.string(sha1);
    }).getOrElse(RedisToken.error("ERR cannot generate sha1 sum for script: " + script));
  }

  private RedisToken exists(Request request) {
    DBServerState server = getServerState(request.getServerContext());
    return integer(server.getScript(request.getParam(1)).isPresent());
  }

  private RedisToken flush(Request request) {
    getServerState(request.getServerContext()).cleanScripts();
    return RedisToken.responseOk();
  }

  private String digest(SafeString script) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    return new SafeString(digest.digest(script.getBytes())).toHexString();
  }

  private Matcher1<Request> isCommand(String command) {
    return request -> request.getParam(0).toString().toLowerCase().equals(command);
  }
}
