/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.resp.util.Precondition.checkNonNull;

public enum DataType {
  STRING("string"),
  LIST("list"),
  SET("set"),
  ZSET("zset"),
  HASH("hash"),
  NONE("none");

  private final String text;

  DataType(String text) {
    this.text = checkNonNull(text);
  }

  public String text() {
    return text;
  }
}
