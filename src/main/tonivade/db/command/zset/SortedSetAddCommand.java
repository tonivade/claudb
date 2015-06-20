/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.zset;

import static java.lang.Float.parseFloat;
import static tonivade.db.data.DatabaseValue.score;
import static tonivade.db.data.DatabaseValue.zset;

import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.db.data.SortedSet;

@Command("zadd")
@ParamLength(3)
@ParamType(DataType.ZSET)
public class SortedSetAddCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseValue initial = db.getOrDefault(request.getParam(0), zset());
            DatabaseValue result = db.merge(request.getParam(0), parseInput(request),
                    (oldValue, newValue) -> {
                        Set<Entry<Double, String>> merge = new SortedSet();
                        merge.addAll(oldValue.getValue());
                        merge.addAll(newValue.getValue());
                        return zset(merge);
                    });
            response.addInt(changed(initial.getValue(), result.getValue()));
        } catch (NumberFormatException e) {
            response.addError("ERR value is not a valid float");
        }
    }

    private int changed(Set<Entry<Float, String>> input, Set<Entry<Float, String>> result) {
        return result.size() - input.size();
    }

    private DatabaseValue parseInput(IRequest request) throws NumberFormatException {
        Set<Entry<Double, String>> set = new SortedSet();
        String score = null;
        for (String string : request.getParams().stream().skip(1).collect(Collectors.toList())) {
            if (score != null) {
                set.add(score(parseFloat(score), string));
                score =  null;
            } else {
                score = string;
            }
        }
        return zset(set);
    }

}
