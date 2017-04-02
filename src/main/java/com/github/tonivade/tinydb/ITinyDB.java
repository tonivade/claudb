/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.github.tonivade.resp.command.IServerContext;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.data.Database;

public interface ITinyDB extends IServerContext {

  int DEFAULT_PORT = 7081;
  String DEFAULT_HOST = "localhost";

  boolean isMaster();
  void setMaster(boolean master);
  void importRDB(InputStream input) throws IOException;
  void exportRDB(OutputStream output) throws IOException;
  Database getDatabase(int i);
  Database getAdminDatabase();
  void publish(String sourceKey, RedisToken message);
  List<List<RedisToken>> getCommandsToReplicate();
}