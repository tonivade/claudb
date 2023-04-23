/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scan;

import static com.github.tonivade.resp.util.Precondition.checkPositive;

import com.github.tonivade.claudb.glob.GlobPattern;
import com.github.tonivade.resp.command.Request;

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
        count = request.getOptionalParam(++i).map(Object::toString).map(Integer::parseInt)
          .orElse(DEFAULT_COUNT);
      }
    }
    return count;
  }

  GlobPattern parsePattern(Request request) {
    GlobPattern pattern = null;
    for (int i = skip; i < request.getLength(); i++) {
      if (request.getParam(i).toString().equalsIgnoreCase(MATCH)) {
        pattern = request.getOptionalParam(++i).map(Object::toString).map(GlobPattern::new)
          .orElse(null);
      }
    }
    return pattern;
  }
}
