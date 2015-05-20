package tonivade.db.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class ResponseTest {

    private Response response;

    @Before
    public void setUp() throws Exception {
        response = new Response();
    }

    @Test
    public void testAddValue() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddBulkStr() {
        assertThat(response.addBulkStr("test").toString(), is("$4\r\ntest\r\n"));
    }

    @Test
    public void testAddSimpleStr() {
        assertThat(response.addSimpleStr("test").toString(), is("+test\r\n"));
    }

    @Test
    public void testAddIntString() {
        assertThat(response.addInt("1").toString(), is(":1\r\n"));
    }

    @Test
    public void testAddIntInt() {
        assertThat(response.addInt(1).toString(), is(":1\r\n"));
    }

    @Test
    public void testAddIntBoolean() {
        assertThat(response.addInt(true).toString(), is(":1\r\n"));
    }

    @Test
    public void testAddError() {
        assertThat(response.addError("ERROR").toString(), is("-ERROR\r\n"));
    }

    @Test
    public void testAddArrayValue() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddArray() {
        assertThat(response.addArray(Arrays.asList("1", "2", "3")).toString(), is("*3\r\n$1\r\n1\r\n$1\r\n2\r\n$1\r\n3\r\n"));
    }

}
