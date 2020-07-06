package com.gestankbratwurst.avarioncore.economy.impl;

import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.economy.abstraction.Tradable;
import com.gestankbratwurst.avarioncore.util.common.UtilItem;
import com.google.gson.JsonObject;
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

  public ItemTradable(final JsonObject jsonObject) {
    this.costToBuy = jsonObject.get("CostToBuy").getAsDouble();
    this.costToSell = jsonObject.get("CostToSell").getAsDouble();
    this.tradeItem = UtilItem.deserializeItemStack(jsonObject.get("TradeItem").getAsString());
  }

  public ItemTradable(final ItemStack tradeItem) {
    this.tradeItem = tradeItem;
  }

  public ItemTradable(final ItemStack tradeItem, final boolean buyable, final boolean sellable) {
    this.tradeItem = tradeItem;
    super.buyable = buyable;
    super.sellable = sellable;
  }

  private final ItemStack tradeItem;
  private double costToBuy;
  private double costToSell;

  @Override
  public TradableType getType() {
    return TradableType.ITEM_TRADABLE;
  }

  @Override
  public double getCostToBuy(final AvarionPlayer avarionPlayer) {
    return this.costToBuy;
  }

  @Override
  public ItemStack getRepresentation(final AvarionPlayer player) {
    return null;
  }

  @Override
  public void removeFrom(final AvarionPlayer avarionPlayer, final int amount) {

  }

  @Override
  public void giveTo(final AvarionPlayer avarionPlayer, final int amount) {
    if (this.tradeItem == null) {
      throw new IllegalStateException("Tried to give null itemstack.");
    }
    avarionPlayer.giveItems(this.tradeItem, amount, true);
  }

  @Override
  public JsonObject getAsJson() {
    final JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("CostToBuy", this.costToBuy);
    jsonObject.addProperty("CostToSell", this.costToSell);
    jsonObject.addProperty("TradeItem", UtilItem.serialize(this.tradeItem));

    return jsonObject;
  }

  @Override
  public double getCostToSell(final AvarionPlayer avarionPlayer) {
    return this.costToSell;
  }

}
