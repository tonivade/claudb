/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class RequestTest {

    @Test
    public void testRequest() throws Exception {
        Request request = new Request("a", Arrays.asList("1", "2", "3"));

        assertThat(request.getCommand(), is("a"));
        assertThat(request.getLength(), is(3));
        assertThat(request.getParams(), is(Arrays.asList("1", "2", "3")));
        assertThat(request.getParam(0), is("1"));
        assertThat(request.getParam(1), is("2"));
        assertThat(request.getParam(2), is("3"));
        assertThat(request.toString(), is("a[3]: [1, 2, 3]"));
    }

}
