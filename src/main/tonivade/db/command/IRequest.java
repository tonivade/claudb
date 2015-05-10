package tonivade.db.command;

import java.util.List;

public interface IRequest {

    /**
     * @return the command
     */
    public String getCommand();

    /**
     * @return the params
     */
    public List<String> getParams();

    public String getParam(int i);

    public int getLength();

}