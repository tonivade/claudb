package com.github.tonivade.claudb.command;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.claudb.command.InAnyOrderRedisArrayMatcher.containsInAnyOrder;
import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.claudb.data.DatabaseValue.list;
import static com.github.tonivade.claudb.data.DatabaseValue.score;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.data.DatabaseValue;

public class DBResponseTest {

  @Test
  public void convertsString() {
    RedisToken token = DBResponse.convertValue(string("test"));

    assertThat(token, equalTo(RedisToken.string("test")));
  }

  @Test
  public void convertsSet()
  {
    RedisToken token = DBResponse.convertValue(set(safeString("a"),
                                                       safeString("b"),
                                                       safeString("c")));

    assertThat(token, containsInAnyOrder(RedisToken.string("a"),
                                         RedisToken.string("b"),
                                         RedisToken.string("c")));
  }

  @Test
  public void convertsList()
  {
    RedisToken token = DBResponse.convertValue(list(safeString("a"),
                                                        safeString("b"),
                                                        safeString("c")));

    assertThat(token, equalTo(array(RedisToken.string("a"),
                                    RedisToken.string("b"),
                                    RedisToken.string("c"))));
  }

  @Test
  public void convertsHash()
  {
    RedisToken token = DBResponse.convertValue(hash(entry(safeString("key1"), safeString("value1")),
                                                        entry(safeString("key2"), safeString("value2")),
                                                        entry(safeString("key3"), safeString("value3"))));

    assertThat(token, containsInAnyOrder(RedisToken.string("key1"),
                                         RedisToken.string("value1"),
                                         RedisToken.string("key2"),
                                         RedisToken.string("value2"),
                                         RedisToken.string("key3"),
                                         RedisToken.string("value3")));
  }

  @Test
  public void convertsZset() {
    RedisToken token = DBResponse.convertValue(zset(score(1.0, safeString("a")),
                                                        score(2.0, safeString("b")),
                                                        score(3.0, safeString("c"))));

    assertThat(token, containsInAnyOrder(RedisToken.string("1.0"),
                                         RedisToken.string("a"),
                                         RedisToken.string("2.0"),
                                         RedisToken.string("b"),
                                         RedisToken.string("3.0"),
                                         RedisToken.string("c")));
  }

  @Test
  public void convertsNull() {
    RedisToken token = DBResponse.convertValue((DatabaseValue) null);

    assertThat(token, equalTo(RedisToken.nullString()));
  }
}
