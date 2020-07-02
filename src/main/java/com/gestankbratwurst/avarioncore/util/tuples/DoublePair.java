package com.gestankbratwurst.avarioncore.util.tuples;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 20.04.2020
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class DoublePair<T> {

  public DoublePair(double key, T value) {
    this.keyDouble = key;
    this.value = value;
  }

  @Getter
  private final double keyDouble;
  @Getter
  @Setter
  @Nullable
  private T value;

}
