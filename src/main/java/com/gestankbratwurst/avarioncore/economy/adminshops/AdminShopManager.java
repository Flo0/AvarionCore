package com.gestankbratwurst.avarioncore.economy.adminshops;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionIO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Set;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 07.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class AdminShopManager {

  public AdminShopManager(final AvarionCore avarionCore) {
    this.shopMap = new Object2ObjectOpenHashMap<>();
    this.avarionIO = avarionCore.getAvarionIO();
  }

  private final Map<String, AdminShop> shopMap;
  private final AvarionIO avarionIO;

  public Set<String> getShopNames() {
    return this.shopMap.keySet();
  }

  public AdminShop getAdminShop(final String name) {
    final AdminShop shop = this.shopMap.get(name);
    return shop;
  }

  public AdminShop createShop(final String shopName, final ShopType shopType) {
    final AdminShop shop = new AdminShop(shopName, shopType);

    if (this.shopMap.containsKey(shopName)) {
      return this.shopMap.get(shopName);
    }
    this.shopMap.put(shopName, shop);

    return shop;
  }

  public void deleteShop(final String shopName) {
    this.shopMap.remove(shopName);
  }

  public void flushData() {
    final JsonObject jsonObject = new JsonObject();

    final JsonArray shopArray = new JsonArray();
    for (final AdminShop shop : this.shopMap.values()) {
      shopArray.add(shop.getAsJson());
    }
    jsonObject.add("Shops", shopArray);

    this.avarionIO.saveAdminShops(jsonObject);
  }

  public void loadShops() {
    final JsonObject jsonObject = this.avarionIO.loadAdminShops();
    if (jsonObject != null) {
      for (final JsonElement element : jsonObject.get("Shops").getAsJsonArray()) {
        final AdminShop shop = new AdminShop(element.getAsJsonObject());
        this.shopMap.put(shop.getShopTitle(), shop);
      }
    }
  }

}
