package com.gestankbratwurst.avarioncore.economy.adminshops;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionIO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.crytec.libs.protocol.npc.manager.NpcManager;
import org.bukkit.Location;

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
    this.npcList = new ArrayList<>();
    this.npcManager = avarionCore.getUtilModule().getNpcAPI().getNPCManager();
  }

  private final List<AdminShopNPC> npcList;
  private final Map<String, AdminShop> shopMap;
  private final AvarionIO avarionIO;
  private final NpcManager npcManager;

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

  public AdminShopNPC createNPC(final Location location, final String displayName, final AdminShop adminShop) {
    final AdminShopNPC adminShopNPC = new AdminShopNPC(location, displayName, adminShop);
    this.npcList.add(adminShopNPC);
    this.npcManager.spawnNPC(adminShopNPC);
    return adminShopNPC;
  }

  private void createNPC(final JsonObject jsonObject) {
    final AdminShopNPC adminShopNPC = new AdminShopNPC(jsonObject);
    if (adminShopNPC.getAdminShop() == null) {
      return;
    }
    this.npcList.add(adminShopNPC);
    this.npcManager.spawnNPC(adminShopNPC);
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

    final JsonArray shopNPCArray = new JsonArray();
    for (final AdminShopNPC npc : this.npcList) {
      final JsonObject npcJson = npc.getAsJson();
      if (npcJson != null) {
        shopNPCArray.add(npcJson);
      }
    }
    jsonObject.add("NPCs", shopNPCArray);

    this.avarionIO.saveAdminShops(jsonObject);
  }

  public void loadShops() {
    final JsonObject jsonObject = this.avarionIO.loadAdminShops();
    if (jsonObject != null) {
      for (final JsonElement element : jsonObject.get("Shops").getAsJsonArray()) {
        final AdminShop shop = new AdminShop(element.getAsJsonObject());
        this.shopMap.put(shop.getShopTitle(), shop);
      }
      for (final JsonElement element : jsonObject.get("NPCs").getAsJsonArray()) {
        this.createNPC(element.getAsJsonObject());
      }
    }
  }

}
