/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import static tonivade.db.data.DatabaseValue.string;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.Map.Entry;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;

import tonivade.db.data.DatabaseValue;
import tonivade.redis.protocol.SafeString;

public class DatabaseValueMatchers {

    public static DatabaseValue list(String ... strings) {
        return DatabaseValue.list(SafeString.safeAsList(strings));
    }

    public static DatabaseValue set(String ... strings) {
        return DatabaseValue.set(SafeString.safeAsList(strings));
    }

    public static Entry<SafeString, SafeString> entry(String key, String value) {
        return DatabaseValue.entry(safeString(key), safeString(value));
    }

    public static Entry<Double, SafeString> score(double score, String value) {
        return DatabaseValue.score(score, safeString(value));
    }

    public static Matcher<DatabaseValue> isString(String expected) {
        return IsEqual.equalTo(string(expected));
    }

    public static Matcher<DatabaseValue> isList(String ... expected) {
        return IsEqual.equalTo(list(expected));
    }

    public static Matcher<DatabaseValue> isSet(String ... expected) {
        return IsEqual.equalTo(set(expected));
    }

    public static Matcher<DatabaseValue> notNullValue() {
        return IsNull.notNullValue(DatabaseValue.class);
    }

    public static Matcher<DatabaseValue> nullValue() {
        return IsNull.nullValue(DatabaseValue.class);
    }
}
