/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public class InAnyOrderRedisArrayMatcher extends TypeSafeMatcher<RedisToken> {

  private Set<RedisToken> expected;

  public InAnyOrderRedisArrayMatcher(Set<RedisToken> expected) {
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
      Collection<RedisToken> tokens = array.getValue();
      return tokens.stream().allMatch(expected::contains);
    }
    return false;
  }

  public static InAnyOrderRedisArrayMatcher containsInAnyOrder(RedisToken... tokens) {
    return new InAnyOrderRedisArrayMatcher(Stream.of(tokens).collect(toSet()));
  }
}
