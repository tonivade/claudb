/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.data;

import java.util.Map;

public interface Database extends Map<DatabaseKey, DatabaseValue> {
  boolean rename(DatabaseKey from, DatabaseKey to);
  boolean isType(DatabaseKey key, DataType type);
}
