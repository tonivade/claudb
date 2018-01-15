/*
 * Copyright (c) 2015-2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.github.tonivade.resp.command.Request;

public class TransactionState implements Iterable<Request> {

  private List<Request> requests = new LinkedList<>();

  public void enqueue(Request request) {
    requests.add(request);
  }

  public int size() {
    return requests.size();
  }

  @Override
  public Iterator<Request> iterator() {
    return requests.iterator();
  }
}
