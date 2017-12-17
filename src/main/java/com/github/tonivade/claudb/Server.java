/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import com.github.tonivade.resp.RespServer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Server {
  public static void main(String[] args) throws Exception {
    OptionParser parser = new OptionParser();
    OptionSpec<Void> help = parser.accepts("help", "print help");
    OptionSpec<Void> verbose = parser.accepts("V", "verbose");
    OptionSpec<Void> persist = parser.accepts("P", "persistence (experimental)");
    OptionSpec<Void> offHeap = parser.accepts("O", "off heap memory (experimental)");
    OptionSpec<Void> notifications = parser.accepts("N", "keyspace notifications (experimental)");
    OptionSpec<String> host = parser.accepts("h", "host").withRequiredArg().ofType(String.class)
        .defaultsTo(ClauDB.DEFAULT_HOST);
    OptionSpec<Integer> port = parser.accepts("p", "port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(ClauDB.DEFAULT_PORT);

    OptionSet options = parser.parse(args);

    if (options.has(help)) {
      parser.printHelpOn(System.out);
    } else {
      String optionHost = options.valueOf(host);
      int optionPort = parsePort(options.valueOf(port));
      DBConfig config = parseConfig(options.has(persist), 
                                    options.has(offHeap), 
                                    options.has(notifications));
     
      readBanner().forEach(System.out::println);
      
      System.setProperty("root-level", options.has(verbose) ? "DEBUG": "INFO");
      
      RespServer server = ClauDB.builder().host(optionHost).port(optionPort).config(config).build();
      Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
      server.start();
    }
  }

  private static Stream<String> readBanner()
  {
    InputStream banner = Server.class.getResourceAsStream("/banner.txt");
    return new BufferedReader(new InputStreamReader(banner)).lines();
  }

  private static int parsePort(Integer optionPort) {
    return optionPort != null ? optionPort : DBServerContext.DEFAULT_PORT;
  }

  private static DBConfig parseConfig(boolean persist, boolean offHeap, boolean notifications) {
    DBConfig.Builder builder = DBConfig.builder();
    if (persist) {
      builder.withPersistence();
    }
    if (offHeap) {
      builder.withOffHeapCache();
    }
    if (notifications) {
      builder.withNotifications();
    }
    return builder.build();
  }
}
