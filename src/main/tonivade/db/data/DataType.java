/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

public enum DataType {

    STRING("string"),
    LIST("list"),
    SET("set"),
    ZSET("zset"),
    HASH("hash"),
    NONE("none");

    private String text;

    private DataType(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

}
