/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.server;

import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.ServerContext;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@ReadOnly
@Command("info")
public class InfoCommand implements DBCommand {

  private static final String SHARP = "#";
  private static final String SEPARATOR = ":";
  private static final String DELIMITER = "\r\n";

  private static final String SECTION_KEYSPACE = "keyspace";
  private static final String SECTION_COMMANDSTATS = "commandstats";
  private static final String SECTION_CPU = "cpu";
  private static final String SECTION_STATS = "stats";
  private static final String SECTION_PERSISTENCE = "persistence";
  private static final String SECTION_MEMORY = "memory";
  private static final String SECTION_CLIENTS = "clients";
  private static final String SECTION_REPLICATION = "replication";
  private static final String SECTION_SERVER = "server";

  @Override
  public RedisToken execute(Database db, Request request) {
    Map<String, Map<String, String>> sections = new LinkedHashMap<>();
    Option<SafeString> param = request.getOptionalParam(0);
    if (param.isPresent()) {
      String sectionName = param.get().toString();
      sections.put(sectionName, section(sectionName, request.getServerContext()));
    } else {
      for (String section : allSections()) {
        sections.put(section, section(section, request.getServerContext()));
      }
    }
    return string(safeString(makeString(sections)));
  }

  private String makeString(Map<String, Map<String, String>> sections) {
    StringBuilder sb = new StringBuilder();
    for (Entry<String, Map<String, String>> section : sections.entrySet()) {
      sb.append(SHARP).append(section.getKey()).append(DELIMITER);
      for (Entry<String, String> entry : section.getValue().entrySet()) {
        sb.append(entry.getKey()).append(SEPARATOR).append(entry.getValue()).append(DELIMITER);
      }
      sb.append(DELIMITER);
    }
    sb.append(DELIMITER);
    return sb.toString();
  }

  private List<String> allSections() {
    return asList(SECTION_SERVER, SECTION_REPLICATION, SECTION_CLIENTS,
        SECTION_MEMORY, SECTION_PERSISTENCE, SECTION_STATS, SECTION_CPU,
        SECTION_COMMANDSTATS, SECTION_KEYSPACE);
  }

  private Map<String, String> section(String section, ServerContext ctx) {
    switch (section.toLowerCase()) {
    case SECTION_SERVER:
      return server(ctx);
    case SECTION_REPLICATION:
      return replication(ctx);
    case SECTION_CLIENTS:
      return clients(ctx);
    case SECTION_MEMORY:
      return memory(ctx);
    case SECTION_PERSISTENCE:
      return persistence(ctx);
    case SECTION_STATS:
      return stats(ctx);
    case SECTION_CPU:
      return cpu(ctx);
    case SECTION_COMMANDSTATS:
      return commandstats(ctx);
    case SECTION_KEYSPACE:
      return keyspace(ctx);
    default:
      break;
    }
    return null;
  }

  private Map<String, String> server(ServerContext ctx) {
    return map(entry("redis_version", "2.8.24"),
               entry("tcp_port", valueOf(ctx.getPort())),
               entry("os", fullOsName()),
               entry("java_version", javaVersion()));
  }

  private String javaVersion() {
    return System.getProperty("java.version");
  }

  private String fullOsName() {
    return System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch");
  }

  private Map<String, String> replication(ServerContext ctx) {
    return map(entry("role", getServerState(ctx).isMaster() ? "master" : "slave"),
        entry("connected_slaves", slaves(ctx)));
  }

  private String slaves(ServerContext ctx) {
    return valueOf(getAdminDatabase(ctx).getSet(safeString("slaves")).size());
  }

  private Map<String, String> clients(ServerContext ctx) {
    return map(entry("connected_clients", valueOf(ctx.getClients())));
  }

  private Map<String, String> memory(ServerContext ctx) {
    return map(entry("used_memory", valueOf(Runtime.getRuntime().totalMemory())));
  }

  private Map<String, String> persistence(ServerContext ctx) {
    // TODO:
    return map();
  }

  private Map<String, String> stats(ServerContext ctx) {
    // TODO:
    return map();
  }

  private Map<String, String> cpu(ServerContext ctx) {
    // TODO:
    return map();
  }

  private Map<String, String> commandstats(ServerContext ctx) {
    // TODO:
    return map();
  }

  private Map<String, String> keyspace(ServerContext ctx) {
    // TODO:
    return map();
  }

  @SafeVarargs
  private static Map<String, String> map(Entry<String, String> ... values) {
    return Stream.of(values)
        .collect(collectingAndThen(toMap(Entry::getKey, Entry::getValue), TreeMap<String, String>::new));
  }

  public static Entry<String, String> entry(String key, String value) {
    return new SimpleEntry<>(key, value);
  }
}
