/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.purefun.Precondition.checkNonNull;

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
