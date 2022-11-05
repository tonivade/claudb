/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import com.github.tonivade.resp.RespServer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Server {

  public static void main(String[] args) throws IOException {
    OptionParser parser = new OptionParser();
    OptionSpec<Void> help = parser.accepts("help", "print help");
    OptionSpec<Void> verbose = parser.accepts("V", "verbose");
    OptionSpec<String> persist = parser.accepts("P", "persistence (experimental)").withRequiredArg();
    OptionSpec<Void> offHeap = parser.accepts("O", "off heap memory (experimental)");
    OptionSpec<Void> notifications = parser.accepts("N", "keyspace notifications (experimental)");
    OptionSpec<String> host = parser.accepts("h", "host")
        .withRequiredArg().defaultsTo(DBServerContext.DEFAULT_HOST);
    OptionSpec<String> port = parser.accepts("p", "port").withRequiredArg();

    OptionSet options = parser.parse(args);

    if (options.has(help)) {
      parser.printHelpOn(System.out);
    } else {
      String optionHost = options.valueOf(host);
      int optionPort = parsePort(options.valueOf(port));
      DBConfig.Builder config = parseConfig(options, persist, offHeap, notifications);

      readBanner().forEach(System.out::println);

      System.setProperty("root-level", options.has(verbose) ? "DEBUG": "INFO");

      RespServer server = ClauDB.builder().host(optionHost).port(optionPort).config(config).build();
      Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
      server.start();
    }
  }

  private static Stream<String> readBanner() {
    InputStream banner = Server.class.getResourceAsStream("/banner.txt");
    return new BufferedReader(new InputStreamReader(banner)).lines();
  }

  private static int parsePort(String optionPort) {
    return optionPort != null ? Integer.parseInt(optionPort) : DBServerContext.DEFAULT_PORT;
  }

  private static DBConfig.Builder parseConfig(OptionSet options, OptionSpec<String> persist, OptionSpec<Void> offHeap, OptionSpec<Void> notifications) {
    DBConfig.Builder builder = DBConfig.builder();
    if (options.has(persist)) {
      builder.withPersistence(options.valueOf(persist));
    }
    if (options.has(offHeap)) {
      builder.withOffHeapCache();
    }
    if (options.has(notifications)) {
      builder.withNotifications();
    }
    return builder;
  }
}
