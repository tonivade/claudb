package tonivade.db.command;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;

public class ResponseTest {

    private Response response;

    @Before
    public void setUp() throws Exception {
        response = new Response();
    }

    @Test
    public void testAddValue() {
        assertThat(response.addValue(value("test")).toString(), is("$4\r\ntest\r\n"));
    }

    @Test
    public void testAddValueNull() throws Exception {
        assertThat(response.addValue(null).toString(), is("$-1\r\n"));
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
    public void testAddIntBooleanTrue() {
        assertThat(response.addInt(true).toString(), is(":1\r\n"));
    }

    @Test
    public void testAddIntBooleanFalse() {
        assertThat(response.addInt(false).toString(), is(":0\r\n"));
    }

    @Test
    public void testAddError() {
        assertThat(response.addError("ERROR").toString(), is("-ERROR\r\n"));
    }

    @Test
    public void testAddArrayValue() {
        List<DatabaseValue> array = Arrays.asList(value("1"), value("2"), value("3"));
        assertThat(response.addArrayValue(array).toString(), is("*3\r\n$1\r\n1\r\n$1\r\n2\r\n$1\r\n3\r\n"));
    }

    @Test
    public void testAddArrayValueNull() {
        assertThat(response.addArrayValue(null).toString(), is("*0\r\n"));
    }

    @Test
    public void testAddArray() {
        List<String> array = Arrays.asList("1", "2", "3");
        assertThat(response.addArray(array).toString(), is("*3\r\n$1\r\n1\r\n$1\r\n2\r\n$1\r\n3\r\n"));
    }

    @Test
    public void testAddArrayNull() {
        assertThat(response.addArray(null).toString(), is("*0\r\n"));
    }

    private DatabaseValue value(String value) {
        return new DatabaseValue(DataType.STRING, value);
    }

}
