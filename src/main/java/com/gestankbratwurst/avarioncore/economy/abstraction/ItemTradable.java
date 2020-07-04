package com.gestankbratwurst.avarioncore.economy.abstraction;

import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 04.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ItemTradable extends Tradable {

  public ItemTradable(final ItemStack tradeItem) {
    super(true, true);
    this.tradeItem = tradeItem;
  }

  public ItemTradable(final ItemStack tradeItem, final boolean buyable, final boolean sellable) {
    super(buyable, sellable);
    this.tradeItem = tradeItem;
  }

  private final ItemStack tradeItem;
  private double costToBuy;
  private double costToSell;

  @Override
  public double getCostToBuy(final AvarionPlayer avarionPlayer) {
    return this.costToBuy;
  }

  @Override
  public ItemStack getRepresentation(final Player player) {
    // TODO edit
    return this.tradeItem;
  }

  @Override
  public void giveTo(final AvarionPlayer avarionPlayer) {
    
  }

  @Override
  public JsonObject getAsJson() {
    return null;
  }

  @Override
  public void loadFromJson(final JsonObject jsonObject) {

  }

  @Override
  public double getCostToSell(final AvarionPlayer avarionPlayer) {
    return 0;
  }
}
