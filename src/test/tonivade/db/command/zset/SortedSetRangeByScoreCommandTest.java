/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.zset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.score;
import static tonivade.db.data.DatabaseValue.zset;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(SortedSetRangeByScoreCommand.class)
public class SortedSetRangeByScoreCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<String>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "1", "3")
            .execute()
            .verify().addArray(captor.capture());

        Collection<String> array = captor.getValue();

        assertThat(array.size(), is(3));

        Iterator<String> iter = array.iterator();

        assertThat(iter.next(), is("a"));
        assertThat(iter.next(), is("b"));
        assertThat(iter.next(), is("c"));
    }

    @Test
    public void testExecuteWithScores() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "1", "3", "WITHSCORES")
            .execute()
            .verify().addArray(captor.capture());

        Collection<String> array = captor.getValue();

        assertThat(array.size(), is(6));

        Iterator<String> iter = array.iterator();

        assertThat(iter.next(), is("1.0"));
        assertThat(iter.next(), is("a"));
        assertThat(iter.next(), is("2.0"));
        assertThat(iter.next(), is("b"));
        assertThat(iter.next(), is("3.0"));
        assertThat(iter.next(), is("c"));
    }

    @Test
    public void testExecuteExclusive() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "(1", "3")
            .execute()
            .verify().addArray(captor.capture());

        Collection<String> array = captor.getValue();

        assertThat(array.size(), is(2));

        Iterator<String> iter = array.iterator();

        assertThat(iter.next(), is("b"));
        assertThat(iter.next(), is("c"));
    }

    @Test
    public void testExecuteInfinity() throws Exception {
        rule.withData("key", zset(score(1, "a"), score(2, "b"), score(3, "c")))
            .withParams("key", "-inf", "+inf")
            .execute()
            .verify().addArray(captor.capture());

        Collection<String> array = captor.getValue();

        assertThat(array.size(), is(3));

        Iterator<String> iter = array.iterator();

        assertThat(iter.next(), is("a"));
        assertThat(iter.next(), is("b"));
        assertThat(iter.next(), is("c"));
    }

}
