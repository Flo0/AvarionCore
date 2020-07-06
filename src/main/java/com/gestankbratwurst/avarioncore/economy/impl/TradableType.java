package com.gestankbratwurst.avarioncore.economy.impl;

import com.gestankbratwurst.avarioncore.economy.abstraction.Tradable;
import com.google.gson.JsonObject;
import java.util.function.Function;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public enum TradableType {

  ITEM_TRADABLE(ItemTradable::new);

  @Getter
  private final Function<JsonObject, Tradable> supplier;

}
