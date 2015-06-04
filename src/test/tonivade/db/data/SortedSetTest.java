/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import static java.util.Collections.unmodifiableSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.score;

import org.junit.Test;

public class SortedSetTest {

    @Test
    public void testName() throws Exception {
        SortedSet set = new SortedSet();

        assertThat(set.add(score(1, "a")), is(true));
        assertThat(set.add(score(2, "a")), is(false));
        assertThat(set.add(score(2, "b")), is(true));

        assertThat(set.contains("a"), is(true));
        assertThat(set.contains("b"), is(true));
        assertThat(set.contains("c"), is(false));

        assertThat(set.score("a"), is(1.0F));
        assertThat(set.score("b"), is(2.0F));

        assertThat(set.ranking("a"), is(0));
        assertThat(set.ranking("b"), is(1));

        assertThat(set.remove("a"), is(true));
        assertThat(set.contains("a"), is(false));
    }

    @Test
    public void testEquals() throws Exception {
        SortedSet setA = new SortedSet();
        setA.add(score(1, "a"));
        setA.add(score(2, "b"));

        SortedSet setB = new SortedSet();
        setB.add(score(1, "a"));
        setB.add(score(2, "b"));

        assertThat(setA, is(setB));
        assertThat(unmodifiableSet(setA), is(unmodifiableSet(setB)));
    }

    @Test
    public void testNotEquals() throws Exception {
        SortedSet setA = new SortedSet();
        setA.add(score(1, "a"));

        SortedSet setB = new SortedSet();
        setB.add(score(1, "a"));
        setB.add(score(2, "b"));

        assertThat(setA, not(is(setB)));
    }

}
