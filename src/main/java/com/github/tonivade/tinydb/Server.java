/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb;

import static com.github.tonivade.tinydb.TinyDBConfig.withPersistence;
import static com.github.tonivade.tinydb.TinyDBConfig.withoutPersistence;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Server {
  public static void main(String[] args) throws Exception {
    OptionParser parser = new OptionParser();
    OptionSpec<Void> help = parser.accepts("help", "print help");
    OptionSpec<Void> persist = parser.accepts("P", "with persistence");
    OptionSpec<String> host = parser.accepts("h", "host").withRequiredArg().ofType(String.class)
        .defaultsTo(TinyDB.DEFAULT_HOST);
    OptionSpec<Integer> port = parser.accepts("p", "port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(TinyDB.DEFAULT_PORT);

    OptionSet options = parser.parse(args);

    if (options.has(help)) {
      parser.printHelpOn(System.out);
    } else {
      String optionHost = options.valueOf(host);
      int optionPort = parsePort(options.valueOf(port));
      TinyDBConfig optionPesistence = parseConfig(options.has(persist));
      TinyDB server = new TinyDB(optionHost, optionPort, optionPesistence);
      server.start();

      Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
    }
  }

  private static int parsePort(Integer optionPort) {
    return optionPort != null ? optionPort : ITinyDB.DEFAULT_PORT;
  }

  private static TinyDBConfig parseConfig(boolean persist) {
    TinyDBConfig config = null;
    if (persist) {
      config = withPersistence();
    } else {
      config = withoutPersistence();
    }
    return config;
  }
}
