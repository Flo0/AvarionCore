package com.gestankbratwurst.avarioncore.economy.adminshops;

import com.gestankbratwurst.avarioncore.economy.abstraction.Tradable;
import com.gestankbratwurst.avarioncore.economy.adminshops.guis.AdminShopEditorProvider;
import com.gestankbratwurst.avarioncore.economy.adminshops.guis.AdminShopTradeProvider;
import com.gestankbratwurst.avarioncore.economy.impl.TradableType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import lombok.Getter;
import net.crytec.inventoryapi.SmartInventory;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 04.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class AdminShop {

  protected AdminShop(final JsonObject jsonObject) {
    this(jsonObject.get("ShopTitle").getAsString(), ShopType.valueOf(jsonObject.get("ShopType").getAsString()));
    final JsonObject tradablesObject = jsonObject.get("Tradables").getAsJsonObject();
    for (final Entry<String, JsonElement> entry : tradablesObject.entrySet()) {
      final Tradable tradable = TradableType.valueOf(entry.getKey()).getSupplier().apply(entry.getValue().getAsJsonObject());
      this.tradables.add(tradable);
    }
  }

  protected AdminShop(final String shopTitle, final ShopType shopType) {
    this.shopTitle = shopTitle;
    this.shopType = shopType;
    this.tradables = new ArrayList<>();
    this.editorGUI = SmartInventory.builder().provider(new AdminShopEditorProvider(this)).size(5).title(shopTitle + " - Editor").build();
    this.tradeGUI = SmartInventory.builder().provider(new AdminShopTradeProvider(this)).size(5).title(shopTitle).build();
  }

  @Getter
  private final ShopType shopType;
  @Getter
  private final String shopTitle;
  private final SmartInventory editorGUI;
  private final SmartInventory tradeGUI;
  private final List<Tradable> tradables;

  public void openEditor(final Player player) {
    this.editorGUI.open(player);
  }

  public void openShopView(final Player player) {
    this.tradeGUI.open(player);
  }

  public JsonObject getAsJson() {
    final JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("ShopTitle", this.shopTitle);
    jsonObject.addProperty("ShopType", this.shopType.toString());
    final EnumMap<TradableType, JsonArray> tradableArrays = new EnumMap<>(TradableType.class);

    for (final TradableType type : TradableType.values()) {
      tradableArrays.put(type, new JsonArray());
    }

    for (final Tradable tradable : this.tradables) {
      tradableArrays.get(tradable.getType()).add(tradable.getAsJson());
    }

    final JsonObject tradableObject = new JsonObject();

    for (final TradableType type : TradableType.values()) {
      tradableObject.add(type.toString(), tradableArrays.get(type));
    }

    jsonObject.add("Tradables", tradableObject);

    return jsonObject;
  }

}
