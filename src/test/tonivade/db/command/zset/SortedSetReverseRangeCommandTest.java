/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.zset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.DatabaseValueMatchers.score;
import static tonivade.db.data.DatabaseValue.zset;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.redis.protocol.SafeString;

@CommandUnderTest(SortedSetReverseRangeCommand.class)
public class SortedSetReverseRangeCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<SafeString>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "-1", "0")
            .execute()
            .verify().addArray(captor.capture());

        Collection<SafeString> array = captor.getValue();

        assertThat(array.size(), is(3));

        Iterator<SafeString> iter = array.iterator();

        assertThat(iter.next(), is(safeString("c")));
        assertThat(iter.next(), is(safeString("b")));
        assertThat(iter.next(), is(safeString("a")));
    }

}
