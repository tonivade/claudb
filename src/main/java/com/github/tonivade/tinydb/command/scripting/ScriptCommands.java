/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.is;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerState;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.util.HexUtil;

import io.vavr.control.Try;

@ParamLength(1)
@Command("script")
public class ScriptCommands implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    return Match(request.getParam(0))
        .of(Case($(is(safeString("load"))), ignore -> load(request)),
            Case($(is(safeString("exists"))), ignore -> exists(request)),
            Case($(is(safeString("flush"))), ignore -> flush(request)),
            Case($(), command -> error("Unknown SCRIPT subcommand: " + command)));
  }

  private RedisToken load(Request request) {
    SafeString script = request.getParam(1);
    String sha1 = Try.of(() -> digest(script)).get();
    TinyDBServerState server = getServerState(request.getServerContext());
    server.saveScript(safeString(sha1), script);
    return RedisToken.string(sha1);
  }

  private RedisToken exists(Request request) {
    TinyDBServerState server = getServerState(request.getServerContext());
    return integer(server.getScript(request.getParam(1)).isPresent());
  }

  private RedisToken flush(Request request) {
    getServerState(request.getServerContext()).cleanScripts();
    return RedisToken.responseOk();
  }

  private String digest(SafeString script) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    return HexUtil.toHexString(digest.digest(script.getBytes()));
  }
}
