package com.github.tonivade.tinydb.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;

public class InAnyOrderRedisArrayMatcher extends TypeSafeMatcher<RedisToken> {

  private List<RedisToken> expected;

  public InAnyOrderRedisArrayMatcher(List<RedisToken> expected)
  {
    this.expected = expected;
  }

  @Override
  public void describeTo(Description description) {
    description.appendValue("should constains in any order: " + expected);
  }

  @Override
  protected boolean matchesSafely(RedisToken item) {
    if (item.getType() == RedisTokenType.ARRAY) {
      List<RedisToken> tokens = item.getValue();
      return new HashSet<>(expected).equals(new HashSet<>(tokens));
    }
    return false;
  }

  public static InAnyOrderRedisArrayMatcher containsInAnyOrder(RedisToken... tokens) {
    return new InAnyOrderRedisArrayMatcher(Arrays.asList(tokens));
  }
}
