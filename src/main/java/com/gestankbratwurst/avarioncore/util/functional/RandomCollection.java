package com.gestankbratwurst.avarioncore.util.functional;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 26.04.2020
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class RandomCollection<E> {
  private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
  private final Random random;
  private double total = 0;

  public RandomCollection() {
    this(new Random());
  }

  public RandomCollection(Random random) {
    this.random = random;
  }

  public RandomCollection<E> add(double weight, E result) {
    if (weight <= 0) return this;
    total += weight;
    map.put(total, result);
    return this;
  }

  public E next() {
    double value = random.nextDouble() * total;
    return map.higherEntry(value).getValue();
  }
}
