package tonivade.db.command.transaction;

import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;

import tonivade.redis.command.IResponse;
import tonivade.redis.command.Response;
import tonivade.redis.protocol.RedisToken;

public class MetaResponse {

    private List<Response> responses = new LinkedList<>();

    public void addResponse(Response response) {
        responses.add(response);
    }

    public List<RedisToken> build() {
        return responses.stream().map(IResponse::build).collect(toList());
    }

}
