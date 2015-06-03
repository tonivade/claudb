/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import java.util.Map;

public interface IDatabase extends Map<String, DatabaseValue> {

    public boolean rename(String from, String to);

    public boolean isType(String key, DataType type);

}
