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

public class ByteBufferOutputStreamTest {

    @Test
    public void testStream() throws Exception {
        ByteBufferOutputStream out =  new ByteBufferOutputStream();

        out.write(9);
        out.write(toByteArray("486F6C61206D756E646F21"));

        byte[] array = out.toByteArray();

        assertThat(toHexString(array), is("09486F6C61206D756E646F21"));

        out.close();
    }

}
