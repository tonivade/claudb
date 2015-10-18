/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import java.util.Map;

public interface IDatabase extends Map<DatabaseKey, DatabaseValue> {

    public boolean rename(DatabaseKey from, DatabaseKey to);

    public boolean isType(DatabaseKey key, DataType type);

    public DatabaseKey overrideKey(DatabaseKey key);

    public DatabaseKey getKey(DatabaseKey key);

}
