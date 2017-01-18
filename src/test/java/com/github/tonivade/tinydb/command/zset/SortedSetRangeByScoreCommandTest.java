/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.zset;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.score;
import static com.github.tonivade.tinydb.data.DatabaseValue.zset;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.zset.SortedSetRangeByScoreCommand;

@CommandUnderTest(SortedSetRangeByScoreCommand.class)
public class SortedSetRangeByScoreCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<?>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "1", "3")
            .execute()
            .verify().addArray(captor.capture());

        Collection<?> array = captor.getValue();

        assertThat(array.size(), is(3));

        Iterator<?> iter = array.iterator();

        assertThat(iter.next(), is(safeString("a")));
        assertThat(iter.next(), is(safeString("b")));
        assertThat(iter.next(), is(safeString("c")));
    }

    @Test
    public void testExecuteWithScores() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "1", "3", "WITHSCORES")
            .execute()
            .verify().addArray(captor.capture());

        Collection<?> array = captor.getValue();

        assertThat(array.size(), is(6));

        Iterator<?> iter = array.iterator();

        assertThat(iter.next(), is(safeString("a")));
        assertThat(iter.next(), is(1.0));
        assertThat(iter.next(), is(safeString("b")));
        assertThat(iter.next(), is(2.0));
        assertThat(iter.next(), is(safeString("c")));
        assertThat(iter.next(), is(3.0));
    }

    @Test
    public void testExecuteWithLimit() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "1", "3", "LIMIT", "1", "2")
            .execute()
            .verify().addArray(captor.capture());

        Collection<?> array = captor.getValue();

        assertThat(array.size(), is(2));

        Iterator<?> iter = array.iterator();

        assertThat(iter.next(), is(safeString("b")));
        assertThat(iter.next(), is(safeString("c")));
    }

    @Test
    public void testExecuteExclusive() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "(1", "3")
            .execute()
            .verify().addArray(captor.capture());

        Collection<?> array = captor.getValue();

        assertThat(array.size(), is(2));

        Iterator<?> iter = array.iterator();

        assertThat(iter.next(), is(safeString("b")));
        assertThat(iter.next(), is(safeString("c")));
    }

    @Test
    public void testExecuteInfinity() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "-inf", "+inf")
            .execute()
            .verify().addArray(captor.capture());

        Collection<?> array = captor.getValue();

        assertThat(array.size(), is(3));

        Iterator<?> iter = array.iterator();

        assertThat(iter.next(), is(safeString("a")));
        assertThat(iter.next(), is(safeString("b")));
        assertThat(iter.next(), is(safeString("c")));
    }

}
