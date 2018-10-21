package com.github.tonivade.claudb.command;

import static java.util.Objects.requireNonNull;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;

public class InAnyOrderRedisArrayMatcher extends TypeSafeMatcher<RedisToken> {

  private ImmutableSet<RedisToken> expected;

  public InAnyOrderRedisArrayMatcher(ImmutableSet<RedisToken> expected) {
    this.expected = requireNonNull(expected);
  }

  @Override
  public void describeTo(Description description) {
    description.appendValue("should constains in any order: " + expected);
  }

  @Override
  protected boolean matchesSafely(RedisToken item) {
    if (item.getType() == RedisTokenType.ARRAY) {
      ArrayRedisToken array = (ArrayRedisToken) item;
      Sequence<RedisToken> tokens = array.getValue();
      return tokens.asSet().equals(expected.asSet());
    }
    return false;
  }

  public static InAnyOrderRedisArrayMatcher containsInAnyOrder(RedisToken... tokens) {
    return new InAnyOrderRedisArrayMatcher(ImmutableSet.of(tokens));
  }
}
