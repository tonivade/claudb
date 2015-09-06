/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import tonivade.db.redis.RedisToken;

public class Client implements ITinyDBCallback {

    private static final String QUIT = "quit";
    private static final String END_OF_LINE = "\r\n";
    private static final String PROMPT = "> ";

    private BlockingQueue<RedisToken> responses = new ArrayBlockingQueue<>(1);

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
         responses.offer(token);
    }

    public RedisToken response() throws InterruptedException {
        return responses.take();
    }

    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser();
        OptionSpec<Void> help = parser.accepts("help", "print help");
        OptionSpec<String> host = parser.accepts("h", "host")
                .withRequiredArg().ofType(String.class).defaultsTo(TinyDB.DEFAULT_HOST);
        OptionSpec<Integer> port = parser.accepts("p", "port")
                .withRequiredArg().ofType(Integer.class).defaultsTo(TinyDB.DEFAULT_PORT);

        OptionSet options = parser.parse(args);

        if (options.has(help)) {
            parser.printHelpOn(System.out);
        } else {
            Client callback = new Client();

            String optionHost = options.valueOf(host);
            int optionPort = parsePort(options.valueOf(port));
            TinyDBClient client = new TinyDBClient(optionHost, optionPort, callback);
            client.start();

            prompt();
            try (Scanner scanner = new Scanner(System.in)) {
                for(boolean quit = false; !quit && scanner.hasNextLine(); prompt()) {
                    String line = scanner.nextLine();
                    client.send(line + END_OF_LINE);
                    System.out.println(callback.response());
                    quit = line.equalsIgnoreCase(QUIT);
                }
            } finally {
                client.stop();
            }
        }
    }

    private static void prompt() {
        System.out.print(PROMPT);
    }

    private static int parsePort(Integer optionPort) {
        return optionPort != null ? optionPort : ITinyDB.DEFAULT_PORT;
    }

}
