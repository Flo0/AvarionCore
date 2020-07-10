package com.gestankbratwurst.avarioncore.economy.adminshops.guis;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionDataManager;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.economy.ItemCostEvaluator;
import com.gestankbratwurst.avarioncore.economy.adminshops.AdminShop;
import com.gestankbratwurst.avarioncore.economy.impl.ItemTradable;
import java.util.Objects;
import net.crytec.inventoryapi.anvil.AnvilGUI;
import net.crytec.inventoryapi.anvil.Response;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
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
public class AdminShopEditorProvider implements InventoryProvider {


  public AdminShopEditorProvider(final AdminShop adminShop) {
    this.adminShop = adminShop;
    this.itemCostEvaluator = AvarionCore.getInstance().getEconomyManager().getItemCostEvaluator();
    this.avarionDataManager = AvarionCore.getInstance().getAvarionDataManager();
  }

  private final AdminShop adminShop;
  private final ItemCostEvaluator itemCostEvaluator;
  private final AvarionDataManager avarionDataManager;

  @Override
  public void init(final Player player, final InventoryContent content) {

  }

  @Override
  public void onClose(final Player player, final InventoryContent content) {

  }


  @Override
  public void onBottomClick(final InventoryClickEvent event) {
    final AvarionPlayer avarionPlayer = this.avarionDataManager.getOnlineData(event.getWhoClicked().getUniqueId());
    final int slot = event.getSlot();
    final Inventory inv = event.getClickedInventory();
    Objects.requireNonNull(inv);
    final ItemStack item = inv.getItem(slot);
    if (item == null) {
      return;
    }

    Objects.requireNonNull(avarionPlayer);

    new AnvilGUI.Builder()
        .onComplete((pl, input) -> {
          final double value;
          try {
            value = Double.parseDouble(input);
          } catch (final NumberFormatException e) {
            avarionPlayer.sendMessage("Shops", "Gib ne ordentliche Kommazahl ein. Du Bob.");
            return Response.close();
          }
          this.adminShop.addTradable(new ItemTradable(item, value));
          this.reopen(pl);
          return Response.close();
        })
        .title("Kosten pro Stück")
        .text("" + this.itemCostEvaluator.getBuyCost(item))
        .open(avarionPlayer.getPlayer());

    event.setCancelled(true);
  }

}
