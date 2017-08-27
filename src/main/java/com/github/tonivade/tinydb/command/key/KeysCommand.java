/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.key;

import static java.util.stream.Collectors.toSet;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

@ReadOnly
@Command("keys")
@ParamLength(1)
public class KeysCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    Pattern pattern = createPattern(request.getParam(0));
    Set<SafeString> keys = db.entrySet().stream()
        .filter(matchPattern(pattern))
        .filter(filterExpired(Instant.now()).negate())
        .map(Map.Entry::getKey)
        .map(DatabaseKey::getValue)
        .collect(toSet());
    return convert(keys);
  }
  
  private Predicate<? super Map.Entry<DatabaseKey, DatabaseValue>> filterExpired(Instant now) {
    return entry -> entry.getValue().isExpired(now);
  }

  private Predicate<? super Map.Entry<DatabaseKey, DatabaseValue>> matchPattern(Pattern pattern) {
    return entry -> pattern.matcher(entry.getKey().toString()).matches();
  }

  private Pattern createPattern(SafeString param) {
    return Pattern.compile(convertGlobToRegEx(param.toString()));
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
