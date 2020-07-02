package com.gestankbratwurst.avarioncore.util.functional;

import java.util.function.Function;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of CorePlugin and was created at the 08.12.2019
 *
 * CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public interface QuadrupleFunction <P1, P2, P3, P4, R> {

  public abstract R apply(P1 p1, P2 p2, P3 p3, P4 p4);
  public abstract Function<R, ?> andThen();

}
