/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.set;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.db.redis.SafeString;

@ReadOnly
@Command("srandmember")
@ParamLength(1)
@ParamType(DataType.SET)
public class SetRandomMemberCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<SafeString> random = new LinkedList<>();
        db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_SET,
                (oldValue, newValue) -> {
                    List<SafeString> merge = new ArrayList<>(oldValue.<Set<SafeString>>getValue());
                    random.add(merge.get(random(merge)));
                    return set(merge);
                });
        if (random.isEmpty()) {
            response.addBulkStr(null);
        } else {
            response.addBulkStr(random.get(0));
        }
    }

    private int random(List<?> merge) {
        return new Random().nextInt(merge.size());
    }

}
