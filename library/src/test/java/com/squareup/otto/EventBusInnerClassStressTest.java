// Copyright 2012 Square, Inc.
package com.squareup.otto;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Stress test of {@link BasicBus} against inner classes. The anon inner class tests were broken
 * when we switched to weak references.
 *
 * @author Ray Ryan (ray@squareup.com)
 */
public class EventBusInnerClassStressTest {
  public static final int REPS = 1000000;
  boolean called;

  class Sub {
    @Subscribe
    public void in(Object o) {
      called = true;
    }
  }

  Sub sub = new Sub();

  @Test public void eventBusOkayWithNonStaticInnerClass() {
    BasicBus eb = new BasicBus(ThreadEnforcer.NONE);
    eb.register(sub);
    int i = 0;
    while (i < REPS) {
      called = false;
      i++;
      eb.post(nextEvent(i));
      assertTrue("Failed at " + i, called);
    }
  }

  @Test public void eventBusFailWithAnonInnerClass() {
    BasicBus eb = new BasicBus(ThreadEnforcer.NONE);
    eb.register(new Object() {
      @Subscribe
      public void in(String o) {
        called = true;
      }
    });
    int i = 0;
    while (i < REPS) {
      called = false;
      i++;
      eb.post(nextEvent(i));
      assertTrue("Failed at " + i, called);
    }
  }

  @Test public void eventBusNpeWithAnonInnerClassWaitingForObject() {
    BasicBus eb = new BasicBus(ThreadEnforcer.NONE);
    eb.register(new Object() {
      @Subscribe
      public void in(Object o) {
        called = true;
      }
    });
    int i = 0;
    while (i < REPS) {
      called = false;
      i++;
      eb.post(nextEvent(i));
      assertTrue("Failed at " + i, called);
    }
  }

  private static String nextEvent(int i) {
    return String.valueOf(i);
  }
}
