/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.data;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;

import org.junit.Test;

import com.github.tonivade.tinydb.data.DatabaseKey;

public class DatabaseKeyTest {

    @Test
    public void testNoExpirationKey() {
        Instant now = Instant.now();

        DatabaseKey nonExpiredKey = new DatabaseKey(safeString("hola"), null);

        assertThat(nonExpiredKey.isExpired(now), is(false));
        assertThat(nonExpiredKey.timeToLiveMillis(now), is(-1L));
        assertThat(nonExpiredKey.timeToLiveSeconds(now), is(-1));
    }

    @Test
    public void testExpiredKey() throws InterruptedException {
        Instant now = Instant.now();

        DatabaseKey expiredKey = new DatabaseKey(safeString("hola"), now.plusSeconds(10));

        assertThat(expiredKey.isExpired(now), is(false));
        assertThat(expiredKey.timeToLiveSeconds(now), is(10));
        assertThat(expiredKey.timeToLiveMillis(now), is(10000L));


        Instant expired = now.plusSeconds(11);

        assertThat(expiredKey.isExpired(expired), is(true));
        assertThat(expiredKey.timeToLiveMillis(expired), is(-1000L));
        assertThat(expiredKey.timeToLiveSeconds(expired), is(-1));
    }
}
