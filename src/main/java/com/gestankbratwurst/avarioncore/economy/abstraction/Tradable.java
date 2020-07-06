package com.gestankbratwurst.avarioncore.economy.abstraction;

import com.gestankbratwurst.avarioncore.economy.impl.TradableType;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

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

  @Getter
  @Setter
  protected boolean buyable;
  @Getter
  @Setter
  protected boolean sellable;

  public abstract TradableType getType();

  public abstract JsonObject getAsJson();

}
