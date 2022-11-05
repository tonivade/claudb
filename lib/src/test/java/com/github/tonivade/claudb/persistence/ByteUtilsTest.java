package com.github.tonivade.claudb.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;
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
  @MethodSource("lengthValues")
  void lengthToArray(int x) {
    byte[] array = ByteUtils.lengthToByteArray(x);
    int y = ByteUtils.byteArrayToLength(IntStream.range(0, array.length).map(i -> array[i]).iterator()::next);
    int z = ByteUtils.byteArrayToLength(ByteBuffer.wrap(array)::get);

    assertThat(x, equalTo(y));
    assertThat(x, equalTo(z));
  }

  static IntStream intValues() {
    return IntStream.generate(() -> ThreadLocalRandom.current().nextInt()).limit(100);
  }

  static IntStream lengthValues() {
    return IntStream.generate(() -> Math.abs(ThreadLocalRandom.current().nextInt())).limit(100);
  }

  static LongStream longValues() {
    return LongStream.generate(() -> ThreadLocalRandom.current().nextLong()).limit(100);
  }
}
