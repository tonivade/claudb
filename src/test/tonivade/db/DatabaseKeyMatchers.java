/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import static com.github.tonivade.resp.protocol.SafeString.safeString;

import java.time.Instant;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import tonivade.db.data.DatabaseKey;

public class DatabaseKeyMatchers {

    public static DatabaseKey safeKey(String str) {
        return DatabaseKey.safeKey(safeString(str));
    }

    public static DatabaseKey safeKey(String str, long milis) {
        return DatabaseKey.safeKey(safeString(str), milis);
    }

    public static DatabaseKey safeKey(String str, int seconds) {
        return DatabaseKey.safeKey(safeString(str), seconds);
    }

    public static Matcher<DatabaseKey> isExpired() {
        return new KeyExpiredMatcher();
    }

    public static Matcher<DatabaseKey> isNotExpired() {
        return new KeyNotExpiredMatcher();
    }

    private static class KeyExpiredMatcher extends TypeSafeMatcher<DatabaseKey> {

        @Override
        public void describeTo(org.hamcrest.Description description) {
            description.appendText("key is expired");
        }

        @Override
        protected boolean matchesSafely(DatabaseKey item) {
            return item.isExpired(Instant.now());
        }

    }

    private static class KeyNotExpiredMatcher extends TypeSafeMatcher<DatabaseKey> {

        @Override
        public void describeTo(org.hamcrest.Description description) {
            description.appendText("Key is not expired");
        }

        @Override
        protected boolean matchesSafely(DatabaseKey item) {
            return !item.isExpired(Instant.now());
        }

    }

}
