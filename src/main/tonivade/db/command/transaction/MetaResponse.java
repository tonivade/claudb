package tonivade.db.command.transaction;

import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;

import tonivade.redis.command.IResponse;
import tonivade.redis.command.Response;

public class MetaResponse {

    private IResponse parent;

    private List<Response> responses = new LinkedList<>();

    public MetaResponse(IResponse response) {
        this.parent = response;
    }

    public void addResponse(Response response) {
        responses.add(response);
    }

    public void build() {
        parent.addArray(responsesToArray());
    }

    private List<byte[]> responsesToArray() {
        return responses.stream().map(r -> r.getBytes()).collect(toList());
    }

}
