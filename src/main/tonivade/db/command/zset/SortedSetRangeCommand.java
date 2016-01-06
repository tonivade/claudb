/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.zset;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static tonivade.db.data.DatabaseKey.safeKey;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.stream.Stream;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.protocol.SafeString;

@ReadOnly
@Command("zrange")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetRangeCommand implements ITinyDBCommand {

    private static final String PARAM_WITHSCORES = "WITHSCORES";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_ZSET);
            NavigableSet<Entry<Float, SafeString>> set = value.getValue();

            int from = Integer.parseInt(request.getParam(1).toString());
            if (from < 0) {
                from = set.size() + from;
            }
            int to = Integer.parseInt(request.getParam(2).toString());
            if (to < 0) {
                to = set.size() + to;
            }

            List<Object> result = emptyList();
            if (from <= to) {
                Optional<SafeString> withScores = request.getOptionalParam(3);
                if (withScores.isPresent() && withScores.get().toString().equalsIgnoreCase(PARAM_WITHSCORES)) {
                    result = set.stream().skip(from).limit((to - from) + 1).flatMap(
                            (o) -> Stream.of(o.getValue(), o.getKey())).collect(toList());
                } else {
                    result = set.stream().skip(from).limit((to - from) + 1).map(
                            (o) -> o.getValue()).collect(toList());
                }
            }

            response.addArray(result);
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        }
    }

}
