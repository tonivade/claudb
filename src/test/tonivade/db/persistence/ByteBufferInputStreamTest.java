/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.persistence.HexUtil.toByteArray;
import static tonivade.db.persistence.HexUtil.toHexString;

import org.junit.Test;

public class ByteBufferInputStreamTest {

    @Test
    public void testStream() throws Exception {
        ByteBufferInputStream in =  new ByteBufferInputStream(toByteArray("09486F6C61206D756E646F21"));

        assertThat(in.read(), is(9));

        byte[] array = new byte[in.available()];
        int readed = in.read(array);

        assertThat(readed, is(array.length));
        assertThat(toHexString(array), is("486F6C61206D756E646F21"));

        in.close();
    }

}
