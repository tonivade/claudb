/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ByteUtilsTest {

  @ParameterizedTest
  @MethodSource("intValues")
  void intToArray(int x) {
    byte[] array = ByteUtils.toByteArray(x);
    int y = ByteUtils.byteArrayToInt(array);

    assertThat(x, equalTo(y));
  }

  @ParameterizedTest
  @MethodSource("longValues")
  void longToArray(long x) {
    byte[] array = ByteUtils.toByteArray(x);
    long y = ByteUtils.byteArrayToLong(array);

    assertThat(x, equalTo(y));
  }

  @ParameterizedTest
  @MethodSource("smallLengthValues")
  void smallLengthToArray(int x) {
    byte[] array = ByteUtils.lengthToByteArray(x);
    assertThat(array.length, equalTo(1));
    int y = ByteUtils.byteArrayToLength(IntStream.range(0, array.length).map(i -> array[i]).iterator()::next);
    int z = ByteUtils.byteArrayToLength(ByteBuffer.wrap(array)::get);

    assertThat(x, equalTo(y));
    assertThat(x, equalTo(z));
  }

  @ParameterizedTest
  @MethodSource("mediumLengthValues")
  void mediumLengthToArray(int x) {
    byte[] array = ByteUtils.lengthToByteArray(x);
    assertThat(array.length, equalTo(2));
    int y = ByteUtils.byteArrayToLength(IntStream.range(0, array.length).map(i -> array[i]).iterator()::next);
    int z = ByteUtils.byteArrayToLength(ByteBuffer.wrap(array)::get);

    assertThat(x, equalTo(y));
    assertThat(x, equalTo(z));
  }

  @ParameterizedTest
  @MethodSource("lengthValues")
  void lengthToArray(int x) {
    byte[] array = ByteUtils.lengthToByteArray(x);
    assertThat(array.length, equalTo(5));
    int y = ByteUtils.byteArrayToLength(IntStream.range(0, array.length).map(i -> array[i]).iterator()::next);
    int z = ByteUtils.byteArrayToLength(ByteBuffer.wrap(array)::get);

    assertThat(x, equalTo(y));
    assertThat(x, equalTo(z));
  }

  static IntStream intValues() {
    return IntStream.generate(() -> current().nextInt()).limit(100);
  }

  static IntStream smallLengthValues() {
    return IntStream.generate(() -> Math.abs(current().nextInt(0x40))).limit(100);
  }

  static IntStream mediumLengthValues() {
    return IntStream.generate(() -> Math.abs(current().nextInt(0x40, 0x80))).limit(100);
  }

  static IntStream lengthValues() {
    return IntStream.generate(() -> Math.abs(current().nextInt())).limit(100);
  }

  static LongStream longValues() {
    return LongStream.generate(() -> current().nextLong()).limit(100);
  }
}
