package tonivade.db.command;

import java.util.Collections;
import java.util.List;

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
        return params.get(i);
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
