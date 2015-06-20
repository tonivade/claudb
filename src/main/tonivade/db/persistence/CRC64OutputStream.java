/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

import static tonivade.db.persistence.Util.toByteArray;

import java.io.IOException;
import java.io.OutputStream;

public class CRC64OutputStream extends OutputStream {

    private static final int LOOKUPTABLE_SIZE = 256;
    private static final long POLY64REV = 0xC96C5795D7870F42L;
    private static final long LOOKUPTABLE[] = new long[LOOKUPTABLE_SIZE];

    static {
        for (int b = 0; b < LOOKUPTABLE.length; ++b) {
            long r = b;
            for (int i = 0; i < Long.BYTES; ++i) {
                if ((r & 1) == 1) {
                    r = (r >>> 1) ^ POLY64REV;
                } else {
                    r >>>= 1;
                }
            }

            LOOKUPTABLE[b] = r;
        }
    }

    private OutputStream out;
    private long crc = -1;

    public CRC64OutputStream(OutputStream out) {
        this.out = out;
    }

    /**
     * @param b
     * @throws IOException
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        out.write(b);
        update((byte) (b & 0xFF));
    }

    /**
     * @param b
     * @throws IOException
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        update(b);
    }

    /**
     * @param b
     * @param off
     * @param len
     * @throws IOException
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        update(b, off, len);
    }

    /**
     * @throws IOException
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        out.flush();
    }

    /**
     * @throws IOException
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {
        out.close();
    }

    private void update(byte b) {
        crc = LOOKUPTABLE[(b ^ (int)crc) & 0xFF] ^ (crc >>> 8);
    }

    private void update(byte[] buf) {
        update(buf, 0, buf.length);
    }

    private void update(byte[] buf, int off, int len) {
        int end = off + len;

        while (off < end) {
            crc = LOOKUPTABLE[(buf[off++] ^ (int)crc) & 0xFF] ^ (crc >>> 8);
        }
    }

    public void checksum() throws IOException {
        out.write(toByteArray(~crc));
    }

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return out.hashCode();
    }

    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return out.equals(obj);
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return out.toString();
    }

}
