/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static com.github.tonivade.claudb.DBConfig.DEFAULT_FILENAME;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import com.github.tonivade.resp.RespServer;

import joptsimple.BuiltinHelpFormatter;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Server {

  public static void main(String[] args) throws IOException {
    OptionParser parser = new OptionParser();
    parser.formatHelpWith(new BuiltinHelpFormatter(100, 2));
    OptionSpec<Void> help = parser.accepts("help", "print help");
    OptionSpec<Void> verbose = parser.accepts("V", "verbose mode");
    OptionSpec<String> persist = parser.accepts("P", "enable persistence (experimental)")
        .withOptionalArg()
        .defaultsTo(DEFAULT_FILENAME)
        .describedAs("file name");
    OptionSpec<Void> offHeap = parser.accepts("O", "enable off heap memory (experimental)");
    OptionSpec<Void> notifications = parser.accepts("N", "enable keyspace notifications (experimental)");
    OptionSpec<String> host = parser.accepts("h", "define listen host")
        .withRequiredArg()
        .defaultsTo(DBServerContext.DEFAULT_HOST)
        .describedAs("host");
    OptionSpec<Integer> port = parser.accepts("p", "define listen port")
        .withRequiredArg()
        .ofType(Integer.class)
        .defaultsTo(DBServerContext.DEFAULT_PORT)
        .describedAs("port");

    OptionSet options;
    try {
      options = parser.parse(args);
    } catch (OptionException e) {
      System.out.println("ERROR: " + e.getMessage());
      System.out.println();
      options = parser.parse("--help");
    }

    if (options.has(help)) {
      parser.printHelpOn(System.out);
    } else {
      String optionHost = options.valueOf(host);
      int optionPort = options.valueOf(port);
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
