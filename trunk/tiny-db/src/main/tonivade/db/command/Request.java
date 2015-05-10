package tonivade.db.command;

import java.util.List;

public class Request implements IRequest {

    private String command;

    private List<String> params;

    /* (non-Javadoc)
     * @see tonivade.db.command.IRequest#getCommand()
     */
    @Override
    public String getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /* (non-Javadoc)
     * @see tonivade.db.command.IRequest#getParams()
     */
    @Override
    public List<String> getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(List<String> params) {
        this.params = params;
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
