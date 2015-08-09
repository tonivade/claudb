/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import java.util.Collection;

import tonivade.db.data.DatabaseValue;
import tonivade.db.redis.SafeString;

public interface IResponse {

    public IResponse addValue(DatabaseValue value);

    public IResponse addArrayValue(Collection<DatabaseValue> array);

    public IResponse addArray(Collection<?> array);

    public IResponse addBulkStr(SafeString str);

    public IResponse addSimpleStr(String str);

    public IResponse addInt(SafeString str);

    public IResponse addInt(int value);

    public IResponse addInt(long value);

    public IResponse addInt(boolean value);

    public IResponse addError(String str);

    public void exit();

    boolean isExit();


}