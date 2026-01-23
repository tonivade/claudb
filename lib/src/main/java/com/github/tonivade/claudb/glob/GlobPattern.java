/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.glob;

import static java.util.regex.Pattern.compile;

import java.util.regex.Pattern;

public class GlobPattern {
  private final String rawPattern;
  private final Pattern pattern;

  public GlobPattern(String pattern) {
    this.rawPattern = pattern;
    this.pattern = compile(convertGlobToRegEx(pattern));
  }

  public boolean match(String value) {
    return pattern.matcher(value).matches();
  }

  /**
   * @return the original string pattern used to create this {@link GlobPattern} instance.
   */
  public String pattern() {
    return rawPattern;
  }

  /*
   * taken from
   * http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
   */
  private String convertGlobToRegEx(String line) {
    int strLen = line.length();
    StringBuilder sb = new StringBuilder(strLen);
    boolean escaping = false;
    int inCurlies = 0;
    for (char currentChar : line.toCharArray()) {
      switch (currentChar) {
      case '*':
        if (escaping) {
          sb.append("\\*");
        } else {
          sb.append(".*");
        }
        escaping = false;
        break;
      case '?':
        if (escaping) {
          sb.append("\\?");
        } else {
          sb.append('.');
        }
        escaping = false;
        break;
      case '.':
      case '(':
      case ')':
      case '+':
      case '|':
      case '^':
      case '$':
      case '@':
      case '%':
        sb.append('\\');
        sb.append(currentChar);
        escaping = false;
        break;
      case '\\':
        if (escaping) {
          sb.append("\\\\");
          escaping = false;
        } else {
          escaping = true;
        }
        break;
      case '{':
        if (escaping) {
          sb.append("\\{");
        } else {
          sb.append('(');
          inCurlies++;
        }
        escaping = false;
        break;
      case '}':
        if (inCurlies > 0 && !escaping) {
          sb.append(')');
          inCurlies--;
        } else if (escaping) {
          sb.append("\\}");
        } else {
          sb.append("}");
        }
        escaping = false;
        break;
      case ',':
        if (inCurlies > 0 && !escaping) {
          sb.append('|');
        } else if (escaping) {
          sb.append("\\,");
        } else {
          sb.append(",");
        }
        break;
      default:
        escaping = false;
        sb.append(currentChar);
      }
    }
    return sb.toString();
  }
}
