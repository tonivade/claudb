/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.data.DatabaseValue;

public class TinyDBResponse {

    private final IResponse response;

    public TinyDBResponse(IResponse response) {
        this.response = response;
    }

    public TinyDBResponse addValue(DatabaseValue value) {
        if (value != null) {
            switch (value.getType()) {
            case STRING:
                response.addBulkStr(value.getValue());
                break;
            case HASH:
                Map<SafeString, SafeString> map = value.getValue();
                response.addArray(keyValueList(map));
                break;
            case LIST:
            case SET:
            case ZSET:
                response.addArray(value.getValue());
                break;
            default:
                break;
            }
        } else {
            response.addBulkStr(null);
        }
        return this;
    }

    private List<SafeString> keyValueList(Map<SafeString, SafeString> map) {
        return map.entrySet().stream().flatMap(
                (entry) -> Stream.of(entry.getKey(), entry.getValue())).collect(toList());
    }

    public TinyDBResponse addArrayValue(Collection<DatabaseValue> array) {
        if (array != null) {
            response.addArray(array.stream().map(Optional::ofNullable)
                    .map(op -> op.isPresent() ? op.get().getValue(): null).collect(toList()));
        } else {
            response.addArray(null);
        }
        return this;
    }

}
