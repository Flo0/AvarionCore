package com.gestankbratwurst.avarioncore.economy.abstraction;

import lombok.Getter;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 04.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public abstract class Tradable implements RepresentedObtainable, Buyable, Sellable {

  public Tradable(final boolean buyable, final boolean sellable) {
    this.buyable = buyable;
    this.sellable = sellable;
  }

  @Getter
  protected boolean buyable;
  @Getter
  protected boolean sellable;

}
