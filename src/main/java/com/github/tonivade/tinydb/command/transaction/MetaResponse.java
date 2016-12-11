package com.github.tonivade.tinydb.command.transaction;

import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;

import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.command.Response;
import com.github.tonivade.resp.protocol.RedisToken;

public class MetaResponse {

    private List<Response> responses = new LinkedList<>();

    public void addResponse(Response response) {
        responses.add(response);
    }

    public List<RedisToken> build() {
        return responses.stream().map(IResponse::build).collect(toList());
    }

}
