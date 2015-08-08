/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DatabaseKeyTest {

    @Test
    public void testExpired() throws Exception {
        DatabaseKey nonExpiredKey = DatabaseKey.safeKey("hola");
        assertThat(nonExpiredKey.isExpired(), is(false));

        DatabaseKey expiredKey = DatabaseKey.ttlKey("hola", 500);
        assertThat(expiredKey.isExpired(), is(false));
        Thread.sleep(1000);
        assertThat(expiredKey.isExpired(), is(true));
    }

}
