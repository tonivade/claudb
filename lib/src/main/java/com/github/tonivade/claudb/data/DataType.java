/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static java.util.Objects.requireNonNull;

public enum DataType {
  STRING("string"),
  LIST("list"),
  SET("set"),
  ZSET("zset"),
  HASH("hash"),
  NONE("none");

  private final String text;

  DataType(String text) {
    this.text = requireNonNull(text);
  }

  public String text() {
    return text;
  }
}
