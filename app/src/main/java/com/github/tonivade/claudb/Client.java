/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.resp.RespCallback;
import com.github.tonivade.resp.RespClient;
import com.github.tonivade.resp.protocol.RedisToken;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Client implements RespCallback {

  private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

  private static final String CHARSET_NAME = "UTF-8";
  private static final String QUIT = "quit";
  private static final String PROMPT = "> ";

  private final BlockingQueue<RedisToken> responses = new ArrayBlockingQueue<>(1);

  @Override
  public void onConnect() {
    System.out.println("connected!");
  }

  @Override
  public void onDisconnect() {
    System.out.println("disconnected!");
  }

  @Override
  public void onMessage(RedisToken token) {
    try {
      responses.put(token);
    } catch (InterruptedException e) {
      LOGGER.warn("message not processed", e);
    }
  }

  public RedisToken response() throws InterruptedException {
    return responses.take();
  }

  public static void main(String[] args) throws Exception {
    OptionParser parser = new OptionParser();
    OptionSpec<Void> help = parser.accepts("help", "print help");
    OptionSpec<String> host = parser.accepts("h", "host")
            .withRequiredArg().defaultsTo(ClauDB.DEFAULT_HOST);
    OptionSpec<String> port = parser.accepts("p", "port").withRequiredArg();

    OptionSet options = parser.parse(args);

    if (options.has(help)) {
      parser.printHelpOn(System.out);
    } else {
      Client callback = new Client();

      String optionHost = options.valueOf(host);
      int optionPort = parsePort(options.valueOf(port));
      RespClient client = new RespClient(optionHost, optionPort, callback);
      client.start();

      prompt();
      try (Scanner scanner = new Scanner(System.in, CHARSET_NAME)) {
        for (boolean quit = false; !quit && scanner.hasNextLine(); prompt()) {
          String line = scanner.nextLine();
          if (!line.isEmpty()) {
            client.send(line.split(" "));
            System.out.println(callback.response());
            quit = line.equalsIgnoreCase(QUIT);
          }
        }
      } finally {
        client.stop();
      }
    }
  }

  private static void prompt() {
    System.out.print(PROMPT);
  }

  private static int parsePort(String optionPort) {
    return optionPort != null ? Integer.parseInt(optionPort) : DBServerContext.DEFAULT_PORT;
  }
}
