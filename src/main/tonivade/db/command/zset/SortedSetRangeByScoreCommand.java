/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.zset;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.score;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.stream.Stream;

import tonivade.db.command.IRedisCommand;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;
import tonivade.server.protocol.SafeString;

@ReadOnly
@Command("zrangebyscore")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetRangeByScoreCommand implements IRedisCommand {

    private static final String EXCLUSIVE = "(";
    private static final String MINUS_INFINITY = "-inf";
    private static final String INIFITY = "+inf";
    private static final String PARAM_WITHSCORES = "WITHSCORES";
    private static final String PARAM_LIMIT = "LIMIT";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_ZSET);
            NavigableSet<Entry<Double, SafeString>> set = value.getValue();

            float from = parseRange(request.getParam(1).toString());
            float to = parseRange(request.getParam(2).toString());

            Options options = parseOptions(request);

            Set<Entry<Double, SafeString>> range = set.subSet(
                    score(from, SafeString.EMPTY_STRING), inclusive(request.getParam(1)),
                    score(to, SafeString.EMPTY_STRING), inclusive(request.getParam(2)));

            List<Object> result = emptyList();
            if (from <= to) {
                if (options.withScores) {
                    result = range.stream().flatMap(
                            (o) -> Stream.of(o.getValue(), o.getKey())).collect(toList());
                } else {
                    result = range.stream().map(
                            (o) -> o.getValue()).collect(toList());
                }

                if (options.withLimit) {
                    result = result.stream().skip(options.offset).limit(options.count).collect(toList());
                }
            }

            response.addArray(result);
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an float or out of range");
        }
    }

    private Options parseOptions(IRequest request) {
        Options options = new Options();
        for (int i = 3; i < request.getLength(); i++) {
            String param = request.getParam(i).toString();
            if (param.equalsIgnoreCase(PARAM_LIMIT)) {
                options.withLimit = true;
                options.offset = parseInt(request.getParam(++i).toString());
                options.count = parseInt(request.getParam(++i).toString());
            } else if (param.equalsIgnoreCase(PARAM_WITHSCORES)) {
                options.withScores = true;
            }
        }
        return options;
    }

    private boolean inclusive(SafeString param) {
        return !param.toString().startsWith(EXCLUSIVE);
    }

    private float parseRange(String param) throws NumberFormatException {
        switch (param) {
        case INIFITY:
            return Float.MAX_VALUE;
        case MINUS_INFINITY:
            return Float.MIN_VALUE;
        default:
            if (param.startsWith(EXCLUSIVE)) {
                return Float.parseFloat(param.substring(1));
            }
            return Float.parseFloat(param);
        }
    }

    private static class Options {
        boolean withScores;
        boolean withLimit;
        int offset;
        int count;
    }

}
