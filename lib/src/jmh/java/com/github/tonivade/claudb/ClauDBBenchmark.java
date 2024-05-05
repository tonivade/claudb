/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.github.tonivade.resp.RespServer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class ClauDBBenchmark {
  
  private static RespServer server = ClauDB.builder().build();
  
  static {
    server.start();
  }
  
  private Jedis jedis = new Jedis("localhost", 7081);
  
  @Benchmark
  public void testCommands() {
    jedis.set("a", "b");
    jedis.set("a", "b");
    jedis.set("a", "b");
    jedis.set("a", "b");
  }

  @Benchmark
  public void testPipeline() {
    Pipeline pipeline = jedis.pipelined();
    pipeline.set("a", "b");
    pipeline.set("a", "b");
    pipeline.set("a", "b");
    pipeline.set("a", "b");
    pipeline.sync();
  }
}
