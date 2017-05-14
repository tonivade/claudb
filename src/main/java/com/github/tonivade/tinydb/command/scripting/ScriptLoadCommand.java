/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.SafeString.safeString;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerState;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.util.HexUtil;

import io.vavr.control.Try;

@ParamLength(1)
@Command("script load")
public class ScriptLoadCommand implements TinyDBCommand {

  @Override
  public RedisToken<?> execute(Database db, IRequest request) {
    SafeString script = request.getParam(0);
    String sha1 = Try.of(() -> digest(script)).get();
    TinyDBServerState server = getServerState(request.getServerContext());
    server.saveScript(safeString(sha1), script);
    return RedisToken.string(sha1);
  }

  private String digest(SafeString script) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    return HexUtil.toHexString(digest.digest(script.getBytes()));
  }
}
