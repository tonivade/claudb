/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;

import java.nio.ByteBuffer;

public interface RedisSource {

    public String readLine();

    public ByteBuffer readBytes(int size);

}
