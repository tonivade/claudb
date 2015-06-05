/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.zset;

import static java.util.stream.Collectors.toList;
import static tonivade.db.data.DatabaseValue.zset;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.stream.Stream;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("zrange")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetRangeCommand implements ICommand {

    private static final String PARAM_WITHSCORES = "WITHSCORES";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseValue value = db.getOrDefault(request.getParam(0), zset());
            NavigableSet<Entry<Float, String>> set = value.getValue();

            int from = Integer.parseInt(request.getParam(1));
            if (from < 0) {
                from = set.size() + from;
            }
            int to = Integer.parseInt(request.getParam(2));
            if (to < 0) {
                to = set.size() + to;
            }

            Optional<String> withScores = request.getOptionalParam(3);

            Entry<?, ?>[] array = set.toArray(new Entry<?, ?>[] {});

            List<String> result = null;
            if (from <= to) {
                if (withScores.isPresent() && withScores.get().equals(PARAM_WITHSCORES)) {
                    result = Stream.of(array).skip(from).limit((to - from) + 1).flatMap((o) -> Stream.of(o.getKey().toString(), o.getValue().toString())).collect(toList());
                } else {
                    result = Stream.of(array).skip(from).limit((to - from) + 1).map((o) -> o.getValue().toString()).collect(toList());
                }
            } else {
                result = Collections.emptyList();
            }

            response.addArray(result);
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        }
    }

}
