package com.gestankbratwurst.avarioncore.economy.adminshops;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 07.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@RequiredArgsConstructor
public enum ShopType {

  SMITH(item -> false),
  BAKER(item -> false);

  private final Predicate<ItemStack> tradabilityFilter;

  public boolean isTradableHere(final ItemStack itemStack) {
    return this.tradabilityFilter.test(itemStack);
  }

}
