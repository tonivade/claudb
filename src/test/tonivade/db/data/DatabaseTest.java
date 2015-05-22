package tonivade.db.data;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;

import org.junit.Test;

public class DatabaseTest {

    private Database database = new Database(new HashMap<String, DatabaseValue>());

    @Test
    public void testDatabase() throws Exception {
        DatabaseValue value = new DatabaseValue(DataType.STRING, "value");
        database.put("a", value);

        assertThat(database.get("a").getValue(), is("value"));
        assertThat(database.containsKey("a"), is(true));
        assertThat(database.containsKey("b"), is(false));
        assertThat(database.isEmpty(), is(false));
        assertThat(database.size(), is(1));
    }

}
