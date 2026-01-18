package com.github.tonivade.claudb.command;

import com.github.tonivade.resp.command.DefaultRequest;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.SafeString;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ParamsScannerTest {

  static final String TEST_NAME1 = "Test-Name-One";
  static final String TEST_NAME1_LOWER = TEST_NAME1.toLowerCase(Locale.ROOT);
  static final String TEST_NAME2 = "Test-Name-Two";
  static final String TEST_NAME2_LOWER = TEST_NAME2.toLowerCase(Locale.ROOT);
  static final String TEST_NAME3 = "Test-Name-Three";
  static final String TEST_STRING_VAL1 = "Test-String-Value-One";
  static final String TEST_STRING_VAL2 = "Test-String-Value-Two";
  static final String TEST_GLOB_VAL1 = "test:glob:one:*";
  static final String TEST_GLOB_VAL2 = "test:glob:two:*";
  static final int TEST_INT_VAL1 = 232102;
  static final int TEST_INT_VAL2 = 535926;

  @Test
  void prerequisites() {
    assertNotEquals(TEST_NAME1, TEST_NAME1_LOWER);
    assertNotEquals(TEST_NAME2, TEST_NAME2_LOWER);
    assertNotEquals(TEST_NAME1, TEST_NAME2);
    assertNotEquals(TEST_STRING_VAL1, TEST_STRING_VAL2);
    assertNotEquals(TEST_INT_VAL1, TEST_INT_VAL2);
    assertNotEquals(TEST_GLOB_VAL1, TEST_GLOB_VAL2);
  }

  @Test
  void remaining() {
    ParamsScanner someParams = ofParams(TEST_STRING_VAL1, TEST_STRING_VAL1);
    assertEquals(2, someParams.remaining());

    someParams.nextString();
    assertEquals(1, someParams.remaining());

    someParams.nextString();
    assertEquals(0, someParams.remaining());
  }

  @Test
  void verifyHasNext() {
    ParamsScanner someParams = ofParams(TEST_NAME1, TEST_STRING_VAL1);
    assertDoesNotThrow(someParams::verifyHasNext);

    someParams.nextString();
    assertDoesNotThrow(someParams::verifyHasNext);

    someParams.nextString();
    CommandException ex = assertThrows(CommandException.class, someParams::verifyHasNext);
    assertEquals("Wrong number of arguments, expected more arguments", ex.getMessage());
  }

  @Test
  void verifyHasNoMore() {
    ParamsScanner someParams = ofParams(TEST_NAME1, TEST_STRING_VAL1);
    CommandException ex = assertThrows(CommandException.class, someParams::verifyHasNoMore);
    assertEquals("Wrong number of arguments, no more arguments expected", ex.getMessage());

    someParams.nextString();
    ex = assertThrows(CommandException.class, someParams::verifyHasNoMore);
    assertEquals("Wrong number of arguments, no more arguments expected", ex.getMessage());

    someParams.nextString();
    assertDoesNotThrow(someParams::verifyHasNoMore);
  }

  @Test
  void hasNext() {
    ParamsScanner someParams = ofParams(TEST_NAME1, TEST_STRING_VAL1);
    assertTrue(someParams.hasNext());

    someParams.nextString();
    assertTrue(someParams.hasNext());

    someParams.nextString();
    assertFalse(someParams.hasNext());
  }

  @Test
  void hasNextValue() {
    ParamsScanner someParams = ofParams(TEST_STRING_VAL1, TEST_NAME1);
    assertFalse(someParams.hasNext(TEST_NAME1));

    someParams.nextString();
    assertTrue(someParams.hasNext(TEST_NAME1));
    assertTrue(someParams.hasNext(TEST_NAME1_LOWER));

    someParams.nextString();
    assertFalse(someParams.hasNext(TEST_NAME1));
    assertFalse(someParams.hasNext(TEST_NAME1_LOWER));
    someParams.verifyHasNoMore();
  }

  @Test
  void nextString() {
    ParamsScanner someParams = ofParams(TEST_NAME1, TEST_STRING_VAL1);
    assertEquals(TEST_NAME1, someParams.nextString());

    assertEquals(TEST_STRING_VAL1, someParams.nextString());

    CommandException ex = assertThrows(CommandException.class, someParams::nextString);
    assertEquals("Wrong number of arguments, expected more arguments", ex.getMessage());
    someParams.verifyHasNoMore();
  }

  @Test
  void nextStringNamed() {
    ParamsScanner someParams = ofParams(TEST_NAME1, TEST_STRING_VAL1, TEST_NAME2, TEST_STRING_VAL2, TEST_NAME3);
    CommandException ex = assertThrows(CommandException.class, () -> someParams.nextString(TEST_NAME2));
    assertEquals("Expected parameter named '" + TEST_NAME2 + "', but found '" + TEST_NAME1 + "'", ex.getMessage());

    assertEquals(TEST_STRING_VAL1, someParams.nextString(TEST_NAME1));
    assertEquals(TEST_STRING_VAL2, someParams.nextString(TEST_NAME2_LOWER));

    ex = assertThrows(CommandException.class, () -> someParams.nextString(TEST_NAME3));
    assertEquals("Value for parameter '" + TEST_NAME3 + "' is missing", ex.getMessage());

    assertEquals(TEST_NAME3, someParams.nextString());

    ex = assertThrows(CommandException.class, () -> someParams.nextString(TEST_NAME1));
    assertEquals("Expected parameter named '" + TEST_NAME1 + "' is missing", ex.getMessage());
    someParams.verifyHasNoMore();
  }

  @Test
  void nextStringNamedDefault() {
    ParamsScanner someParams = ofParams(TEST_NAME1, TEST_STRING_VAL1, TEST_NAME2);
    int expectedRemaining = someParams.remaining();
    assertEquals(TEST_STRING_VAL2, someParams.nextString(TEST_NAME2, TEST_STRING_VAL2), "Default used");
    assertEquals(expectedRemaining, someParams.remaining(), "Parameters not consumed");

    assertEquals(TEST_STRING_VAL1, someParams.nextString(TEST_NAME1, TEST_STRING_VAL1), "Parameter value used");
    expectedRemaining -= 2;
    assertEquals(expectedRemaining, someParams.remaining(), "Two parameters consumed");

    CommandException ex = assertThrows(CommandException.class, () -> someParams.nextString(TEST_NAME2, TEST_STRING_VAL2));
    assertEquals("Value for parameter '" + TEST_NAME2 + "' is missing", ex.getMessage());
    assertEquals(expectedRemaining, someParams.remaining(), "Parameters not consumed");

    someParams.nextString();
    assertEquals(TEST_STRING_VAL2, someParams.nextString(TEST_NAME2, TEST_STRING_VAL2), "Default used again");
    someParams.verifyHasNoMore();
  }

  @Test
  void nextInt() {
    ParamsScanner someParams = ofParams(TEST_INT_VAL1, TEST_INT_VAL2, TEST_STRING_VAL1);
    assertEquals(TEST_INT_VAL1, someParams.nextInt());
    assertEquals(TEST_INT_VAL2, someParams.nextInt());

    CommandException ex = assertThrows(CommandException.class, someParams::nextInt);
    assertEquals("Value is not an integer or out of range", ex.getMessage());

    assertEquals(TEST_STRING_VAL1, someParams.nextString());

    ex = assertThrows(CommandException.class, someParams::nextInt);
    assertEquals("Wrong number of arguments, expected more arguments", ex.getMessage());
    someParams.verifyHasNoMore();
  }

  @Test
  void nextIntNamed() {
    ParamsScanner someParams = ofParams(TEST_NAME1, TEST_INT_VAL1, TEST_NAME2, TEST_INT_VAL2, TEST_NAME3);
    CommandException ex = assertThrows(CommandException.class, () -> someParams.nextInt(TEST_NAME2));
    assertEquals("Expected parameter named '" + TEST_NAME2 + "', but found '" + TEST_NAME1 + "'", ex.getMessage());

    assertEquals(TEST_INT_VAL1, someParams.nextInt(TEST_NAME1));
    assertEquals(TEST_INT_VAL2, someParams.nextInt(TEST_NAME2_LOWER));

    ex = assertThrows(CommandException.class, () -> someParams.nextInt(TEST_NAME3));
    assertEquals("Value for parameter '" + TEST_NAME3 + "' is missing", ex.getMessage());

    assertEquals(TEST_NAME3, someParams.nextString());

    ex = assertThrows(CommandException.class, () -> someParams.nextInt(TEST_NAME1));
    assertEquals("Expected parameter named '" + TEST_NAME1 + "' is missing", ex.getMessage());
    someParams.verifyHasNoMore();
  }

  @Test
  void nextIntNamedDefault() {
    ParamsScanner someParams = ofParams(TEST_NAME1, TEST_INT_VAL1, TEST_NAME2);
    int expectedRemaining = someParams.remaining();
    assertEquals(TEST_INT_VAL2, someParams.nextInt(TEST_NAME2, TEST_INT_VAL2), "Default used");
    assertEquals(expectedRemaining, someParams.remaining(), "Parameters not consumed");

    assertEquals(TEST_INT_VAL1, someParams.nextInt(TEST_NAME1, TEST_INT_VAL1), "Parameter value used");
    expectedRemaining -= 2;
    assertEquals(expectedRemaining, someParams.remaining(), "Two parameters consumed");

    CommandException ex = assertThrows(CommandException.class, () -> someParams.nextInt(TEST_NAME2, TEST_INT_VAL2));
    assertEquals("Value for parameter '" + TEST_NAME2 + "' is missing", ex.getMessage());
    assertEquals(expectedRemaining, someParams.remaining(), "Parameters not consumed");

    someParams.nextString();
    assertEquals(TEST_INT_VAL2, someParams.nextInt(TEST_NAME2, TEST_INT_VAL2), "Default used again");
    someParams.verifyHasNoMore();
  }

  @Test
  void nextGlob() {
    ParamsScanner someParams = ofParams(TEST_GLOB_VAL1, TEST_GLOB_VAL2);
    assertEquals(TEST_GLOB_VAL1, someParams.nextGlob().pattern());
    assertEquals(TEST_GLOB_VAL2, someParams.nextGlob().pattern());

    CommandException ex = assertThrows(CommandException.class, someParams::nextInt);
    assertEquals("Wrong number of arguments, expected more arguments", ex.getMessage());
    someParams.verifyHasNoMore();
  }

  @Test
  void nextGlobNamed() {
    ParamsScanner someParams = ofParams(TEST_NAME1, TEST_GLOB_VAL1, TEST_NAME2, TEST_GLOB_VAL2, TEST_NAME3);
    CommandException ex = assertThrows(CommandException.class, () -> someParams.nextGlob(TEST_NAME2));
    assertEquals("Expected parameter named '" + TEST_NAME2 + "', but found '" + TEST_NAME1 + "'", ex.getMessage());

    assertEquals(TEST_GLOB_VAL1, someParams.nextGlob(TEST_NAME1).pattern());
    assertEquals(TEST_GLOB_VAL2, someParams.nextGlob(TEST_NAME2_LOWER).pattern());

    ex = assertThrows(CommandException.class, () -> someParams.nextGlob(TEST_NAME3));
    assertEquals("Value for parameter '" + TEST_NAME3 + "' is missing", ex.getMessage());

    assertEquals(TEST_NAME3, someParams.nextString());

    ex = assertThrows(CommandException.class, () -> someParams.nextGlob(TEST_NAME1));
    assertEquals("Expected parameter named '" + TEST_NAME1 + "' is missing", ex.getMessage());
    someParams.verifyHasNoMore();
  }

  @Test
  void nextGlobNamedDefault() {
    ParamsScanner someParams = ofParams(TEST_NAME1, TEST_GLOB_VAL1, TEST_NAME2);
    int expectedRemaining = someParams.remaining();
    assertEquals(TEST_GLOB_VAL2, someParams.nextGlob(TEST_NAME2, TEST_GLOB_VAL2).pattern(), "Default used");
    assertEquals(expectedRemaining, someParams.remaining(), "Parameters not consumed");

    assertEquals(TEST_GLOB_VAL1, someParams.nextGlob(TEST_NAME1, TEST_GLOB_VAL1).pattern(), "Parameter used");
    expectedRemaining -= 2;
    assertEquals(expectedRemaining, someParams.remaining(), "Two parameters consumed");

    CommandException ex = assertThrows(CommandException.class, () -> someParams.nextGlob(TEST_NAME2, TEST_GLOB_VAL2));
    assertEquals("Value for parameter '" + TEST_NAME2 + "' is missing", ex.getMessage());
    assertEquals(expectedRemaining, someParams.remaining(), "Parameters not consumed");

    someParams.nextString();
    assertEquals(TEST_GLOB_VAL2, someParams.nextGlob(TEST_NAME2, TEST_GLOB_VAL2).pattern(), "Default used");
    someParams.verifyHasNoMore();
  }

  private static ParamsScanner ofParams(Object... values) {
    List<SafeString> params = Stream.of(values)
      .map(String::valueOf)
      .map(SafeString::safeString)
      .collect(Collectors.toList());
    Request request = new DefaultRequest(mock(), mock(), mock(), params);
    return new ParamsScanner(request);
  }
}