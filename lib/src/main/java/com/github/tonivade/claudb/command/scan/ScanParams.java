/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scan;

import static com.github.tonivade.resp.util.Precondition.checkPositive;

import com.github.tonivade.claudb.glob.GlobPattern;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.Optional;

class ScanParams {

  private static final int DEFAULT_COUNT = 10;
  private static final String COUNT = "count";
  private static final String MATCH = "match";

  private final int skip;

  ScanParams(int skip) {
    this.skip = checkPositive(skip);
  }

  int parseCount(Request request) {
    int count = DEFAULT_COUNT;
    for (int i = skip; i < request.getLength(); i++) {
      if (request.getParam(i).toString().equalsIgnoreCase(COUNT)) {
        Optional<SafeString> optionalParam = request.getOptionalParam(++i);
        if (optionalParam.isPresent()) {
          count = optionalParam.map(Object::toString).map(Integer::parseInt)
            .orElse(DEFAULT_COUNT);
        } else {
          throw new IllegalArgumentException("syntax error");
        }
      }
    }
    return count;
  }

  GlobPattern parsePattern(Request request) {
    GlobPattern pattern = null;
    for (int i = skip; i < request.getLength(); i++) {
      if (request.getParam(i).toString().equalsIgnoreCase(MATCH)) {
        Optional<SafeString> optionalParam = request.getOptionalParam(++i);
        if (optionalParam.isPresent()) {
          pattern = optionalParam.map(Object::toString).map(GlobPattern::new)
            .orElse(null);
        } else {
          throw new IllegalArgumentException("syntax error");
        }
      }
    }
    return pattern;
  }
}
