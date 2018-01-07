/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@Warmup(iterations = 0)
@Measurement(iterations = 5)
@Fork(value = 1)
@Threads(value = 5)
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
public class ClauDBBenchmark {
  
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
