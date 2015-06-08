/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import tonivade.db.data.IDatabase;

public interface IServerContext {

    public void publish(String destination, String message);

    public IDatabase getDatabase();

}
