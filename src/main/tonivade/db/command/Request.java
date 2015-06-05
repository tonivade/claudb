/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Request implements IRequest {

    private String command;

    private List<String> params;

    public Request(String command, List<String> params) {
        super();
        this.command = command;
        this.params = params;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IRequest#getCommand()
     */
    @Override
    public String getCommand() {
        return command;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IRequest#getParams()
     */
    @Override
    public List<String> getParams() {
        return Collections.unmodifiableList(params);
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IRequest#getParam(int)
     */
    @Override
    public String getParam(int i) {
        if (i < params.size()) {
            return params.get(i);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see tonivade.db.command.IRequest#getOptionalParam(int)
     */
    @Override
    public Optional<String> getOptionalParam(int i) {
        return Optional.ofNullable(getParam(i));
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IRequest#getLength()
     */
    @Override
    public int getLength() {
        return params.size();
    }

    @Override
    public String toString() {
        return command + "[" + params.size() + "]: " + params;
    }

}
