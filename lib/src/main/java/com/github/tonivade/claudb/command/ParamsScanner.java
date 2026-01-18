package com.github.tonivade.claudb.command;

import com.github.tonivade.claudb.glob.GlobPattern;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.SafeString;

import java.util.function.Function;

/**
 * A utility class to parse command parameters from a {@link Request}.
 * This class maintains an internal index to track parameter consumption
 * and provides methods for sequential and named parameter parsing.
 * All parameters are validated during access, and {@link CommandException} is thrown
 * if the expected arguments are missing, invalid, or misplaced.
 */
public final class ParamsScanner {
  private final Request request;
  private int index = 0;

  public ParamsScanner(Request request) {
    this.request = request;
  }

  /**
   * @return the number of unprocessed parameters.
   */
  public int remaining() {
    return request.getLength() - index;
  }

  /**
   * @return true if there are more unaccessed parameters; false otherwise
   */
  public boolean hasNext() {
    return index < request.getLength();
  }

  /**
   * Ensures that there are unprocessed parameters,
   * and throws {@link CommandException } if there are no unprocessed parameters.
   */
  public void verifyHasNext() {
    if (index >= request.getLength()) {
      throw new CommandException("Wrong number of arguments, expected more arguments");
    }
  }

  /**
   * Ensures that there are no more unprocessed parameters,
   * and throws {@link CommandException } if there are unprocessed parameters.
   */
  public void verifyHasNoMore() {
    if (index < request.getLength()) {
      throw new CommandException("Wrong number of arguments, no more arguments expected");
    }
  }

  /**
   * Checks if there are more parameters available and if the next parameter matches the given value.
   * In other words, this method checks that string representation of the next parameter is equal to the {@code value}.
   *
   * @param value expected value for the next parameter ignoring a case.
   * @return true if there are unaccessed parameters and the next parameter matches the given name; false otherwise.
   *
   * @see #hasNext()
   */
  public boolean hasNext(String value) {
    return index < request.getLength() && value.equalsIgnoreCase(request.getParam(index).toString());
  }

  /**
   * Consumes one next unprocessed parameter and returns it as a {@link String}.
   * If there are no more parameters to process, {@link CommandException} is thrown.
   *
   * @return the string representation of the next unprocessed parameter.
   */
  public String nextString() {
    return getValue(SafeString::toString);
  }

  /**
   * Consumes two next unprocessed parameters and validates that the first parameter matches {@code name}
   * and returns the second parameter as a {@link String}.
   * If the name does not match or if there are no unprocessed parameters available,
   * a {@link CommandException} is thrown.
   * <p>
   * Speaking simply, it is intended to parse pais of parameter where
   * the first parameter is a name and the second is a value.
   * For example, it can be used to parse the command:<br>
   * {@code HELLO SETNAME redis-cli}
   * <pre>
   * {@code String clientName = paramsScanner.nextString("SETNAME");}
   * </pre>
   *
   * @param name the expected name of the next parameter
   *
   * @return the {@link String} representation of the second consumed parameter.
   * @throws CommandException if the next parameter's name does not match the given name,
   *                          or if there are no more parameters to process.
   */
  public String nextString(String name) {
    return getNamedValue(name, SafeString::toString);
  }

  /**
   * Similar as {@link #nextString(String)} but returns a default value if the parameter is missing.
   *
   * @param name the expected name of the next parameter
   * @param defValue the default value to return if there is only one unprocessed parameter.
   *
   * @return the {@link String} representation of the second consumed parameter or the default value.
   * @throws CommandException if the next parameter's name does not match the given name.
   * @see #nextString(String)
   */
  public String nextString(String name, String defValue) {
    return getOptionalNamedValue(name, defValue, SafeString::toString);
  }

  /**
   * Similar as {@link #nextString()} but returns an integer value.
   *
   * @return the integer value of the next unprocessed parameter,
   * or throws {@link CommandException} if the parameter is not an integer or missing.
   *
   * @see #nextString()
   */
  public int nextInt() {
    return getValue(ParamsScanner::parseInt);
  }

  /**
   * Similar as {@link #nextString(String)} but returns an integer value.
   * @see #nextString(String)
   */
  public int nextInt(String name) {
    return getNamedValue(name, ParamsScanner::parseInt);
  }

  /**
   * Similar as {@link #nextString(String, String)} but returns an integer value.
   * @see #nextString(String, String)
   */
  public int nextInt(String name, int defValue) {
    return getOptionalNamedValue(name, defValue, ParamsScanner::parseInt);
  }

  /**
   * Similar as {@link #nextString()} but returns a {@link GlobPattern} value.
   * @see #nextString()
   */
  public GlobPattern nextGlob() {
    return new GlobPattern(nextString());
  }

  /**
   * Similar as {@link #nextString(String)} but returns a {@link GlobPattern} value.
   * @see #nextString(String)
   */
  public GlobPattern nextGlob(String name) {
    return new GlobPattern(nextString(name));
  }

  /**
   * Similar as {@link #nextString(String, String)} but returns a {@link GlobPattern} value.
   * @see #nextString(String, String)
   */
  public GlobPattern nextGlob(String name, String defValue) {
    return new GlobPattern(nextString(name, defValue));
  }

  private <T> T getValue(Function<SafeString, T> converter) {
    if (request.getLength() <= index) {
      throw new CommandException("Wrong number of arguments, expected more arguments");
    }
    T result = converter.apply(request.getParam(index));
    index++;
    return result;
  }

  private <T> T getNamedValue(String name, Function<SafeString, T> extractor) {
    if (request.getLength() <= index) {
      throw new CommandException("Expected parameter named '" + name + "' is missing");
    }
    SafeString actualName = request.getParam(index);
    if (!name.equalsIgnoreCase(actualName.toString())) {
      throw new CommandException("Expected parameter named '" + name + "', but found '" + actualName + "'");
    }
    if (request.getLength() <= index + 1) {
      throw new CommandException("Value for parameter '" + name + "' is missing");
    }
    T result = extractor.apply(request.getParam(index + 1));
    index += 2;
    return result;
  }

  private <T> T getOptionalNamedValue(String name, T defValue, Function<SafeString, T> extractor) {
    if (request.getLength() <= index) {
      return defValue;
    }
    SafeString actualName = request.getParam(index);
    if (!name.equalsIgnoreCase(actualName.toString())) {
      return defValue;
    }
    if (request.getLength() <= index + 1) {
      throw new CommandException("Value for parameter '" + name + "' is missing");
    }
    T result = extractor.apply(request.getParam(index + 1));
    index += 2;
    return result;
  }

  private static Integer parseInt(SafeString str) {
    try {
      return Integer.parseInt(str.toString());
    } catch (NumberFormatException e) {
      throw new CommandException("Value is not an integer or out of range");
    }
  }

}
