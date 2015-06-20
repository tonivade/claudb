/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.command.persistence.HexUtil.toHexString;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import tonivade.db.persistence.CRC64OutputStream;

public class CRC64OutputStreamTest {

    @Test
    public void testOne() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CRC64OutputStream out = new CRC64OutputStream(baos);
        out.write("123456789".getBytes());
        out.checksum();
        out.close();

        assertThat(toHexString(baos.toByteArray()), is("313233343536373839995DC9BBDF1939FA"));
    }

    @Test
    public void testString() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CRC64OutputStream out = new CRC64OutputStream(baos);
        out.write("This is a test of the emergency broadcast system.".getBytes());
        out.checksum();
        out.close();

        assertThat(toHexString(baos.toByteArray()),
                is("5468697320697320612074657374206F662074686520656D657267656E63792062726F6164636173742073797374656D2E27DB187FC15BBC72"));
    }

}
