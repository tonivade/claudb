/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static tonivade.db.DatabaseKeyMatchers.safeKey;

import org.junit.Test;

public class DatabaseKeyTest {

    @Test
    public void testExpired() throws Exception {
        DatabaseKey nonExpiredKey = safeKey("hola");
        assertThat(nonExpiredKey.isExpired(), is(false));
        assertThat(nonExpiredKey.timeToLive(), is(-1L));

        DatabaseKey expiredKey = safeKey("hola", 1);
        assertThat(expiredKey.isExpired(), is(false));
        assertThat(expiredKey.timeToLive(), is(greaterThan(0L)));
        Thread.sleep(1100);
        assertThat(expiredKey.isExpired(), is(true));
        assertThat(expiredKey.timeToLive(), is(-2L));
    }

}
