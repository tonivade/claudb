/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.persistence.HexUtil.toHexString;

import org.junit.Test;

public class SafeStringTest {

    @Test
    public void testBytes() throws Exception {
        System.out.println("1");
        long nanos = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            SafeString str = SafeString.safeString("Hola Mundo!");

            assertThat(str.length(), is(11));
            assertThat(toHexString(str.getBytes()), is("486F6C61204D756E646F21"));
            assertThat(str.toString(), is("Hola Mundo!"));
        }
        System.out.println(System.nanoTime() - nanos);
    }

}
