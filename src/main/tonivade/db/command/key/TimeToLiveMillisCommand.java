/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import java.time.Instant;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;

import tonivade.db.data.DatabaseKey;

@Command("pttl")
@ParamLength(1)
public class TimeToLiveMillisCommand extends TimeToLiveCommand {

    @Override
    protected int timeToLive(DatabaseKey key, Instant now) {
        return (int) key.timeToLiveMillis(now);
    }

}
