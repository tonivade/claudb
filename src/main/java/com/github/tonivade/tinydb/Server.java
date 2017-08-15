/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Server {
  public static void main(String[] args) throws Exception {
    OptionParser parser = new OptionParser();
    OptionSpec<Void> help = parser.accepts("help", "print help");
    OptionSpec<Void> persist = parser.accepts("P", "persistence (experimental)");
    OptionSpec<Void> offHeap = parser.accepts("O", "off heap memory (experimental)");
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
      TinyDBConfig config = parseConfig(options.has(persist), options.has(offHeap));
     
      readBanner().forEach(System.out::println);
      
      TinyDB server = new TinyDB(optionHost, optionPort, config);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
      server.start();
    }
  }

  private static Stream<String> readBanner() throws IOException, URISyntaxException
  {
    InputStream banner = Server.class.getResourceAsStream("/banner.txt");
    return new BufferedReader(new InputStreamReader(banner)).lines();
  }

  private static int parsePort(Integer optionPort) {
    return optionPort != null ? optionPort : TinyDBServerContext.DEFAULT_PORT;
  }

  private static TinyDBConfig parseConfig(boolean persist, boolean offHeap) {
    TinyDBConfig.Builder builder = TinyDBConfig.builder();
    if (persist) {
      builder.withPersistence();
    }
    if (offHeap) {
      builder.withOffHeapCache();
    }
    return builder.build();
  }
}
