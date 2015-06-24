/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TinyDBRule  implements TestRule {

    private TinyDB server;

    public TinyDBRule() {
        this(ITinyDB.DEFAULT_HOST, ITinyDB.DEFAULT_PORT);
    }

    public TinyDBRule(String host, int port) {
        this.server = new TinyDB(host, port);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    server.start();
                    base.evaluate();
                } finally {
                    server.stop();
                }

            }
        };
    }

}
