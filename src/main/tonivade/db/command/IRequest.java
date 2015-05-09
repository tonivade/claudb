package tonivade.db.command;

import java.util.List;

public interface IRequest {

    /**
     * @return the command
     */
    public abstract String getCommand();

    /**
     * @return the params
     */
    public abstract List<String> getParams();

    public abstract String getParam(int i);

    public abstract int getLength();

}