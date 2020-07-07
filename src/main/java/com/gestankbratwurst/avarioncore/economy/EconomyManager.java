package com.gestankbratwurst.avarioncore.economy;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.economy.adminshops.AdminShopManager;
import lombok.Getter;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 07.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class EconomyManager {

  public EconomyManager(final AvarionCore plugin) {
    this.moneyItemHandler = new MoneyItemHandler(plugin);
    this.itemCostEvaluator = new ItemCostEvaluator();
    this.adminShopManager = new AdminShopManager(plugin);
  }

  @Getter
  private final MoneyItemHandler moneyItemHandler;
  @Getter
  private final ItemCostEvaluator itemCostEvaluator;
  @Getter
  private final AdminShopManager adminShopManager;

}
