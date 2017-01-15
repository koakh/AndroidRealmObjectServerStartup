package com.koakh.androidrealmobjectserverstartup.model;

import io.realm.RealmObject;

public class CounterOperation extends RealmObject {
  public long adjustment;

  public CounterOperation() {
  }

  public CounterOperation(long adjustment) {
    this.adjustment = adjustment;
  }
}
