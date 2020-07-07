package com.gestankbratwurst.avarioncore.economy.adminshops.guis;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionDataManager;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.economy.ItemCostEvaluator;
import com.gestankbratwurst.avarioncore.economy.adminshops.AdminShop;
import com.gestankbratwurst.avarioncore.resourcepack.sounds.CustomSound;
import java.util.Objects;
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
public class AdminShopTradeProvider implements InventoryProvider {

  public AdminShopTradeProvider(final AdminShop adminShop) {
    this.adminShop = adminShop;
    this.itemCostEvaluator = AvarionCore.getInstance().getEconomyManager().getItemCostEvaluator();
    this.avarionDataManager = AvarionCore.getInstance().getAvarionDataManager();
  }

  private final AdminShop adminShop;
  private final ItemCostEvaluator itemCostEvaluator;
  private final AvarionDataManager avarionDataManager;

  @Override
  public void init(final Player player, final InventoryContent content) {
    final AvarionPlayer avPlayer = AvarionCore.getInstance().getAvarionDataManager().getOnlineData(player.getUniqueId());
    Objects.requireNonNull(avPlayer);
    avPlayer.setAsShopping(this.adminShop);
  }

  @Override
  public void onClose(final Player player, final InventoryContent content) {
    final AvarionPlayer avPlayer = AvarionCore.getInstance().getAvarionDataManager().getOnlineData(player.getUniqueId());
    Objects.requireNonNull(avPlayer);
    avPlayer.setAsNoLongerShopping();
  }

  @Override
  public void onBottomClick(final InventoryClickEvent event) {
    System.out.println("Click?");
    final AvarionPlayer avarionPlayer = this.avarionDataManager.getOnlineData(event.getWhoClicked().getUniqueId());
    final int slot = event.getSlot();
    final Inventory inv = event.getClickedInventory();
    Objects.requireNonNull(inv);
    final ItemStack item = inv.getItem(slot);
    if (item == null) {
      return;
    }

    Objects.requireNonNull(avarionPlayer);

    if (this.adminShop.getShopType().isTradableHere(item)) {
      final Player player = (Player) event.getWhoClicked();
      final double basePrice = this.itemCostEvaluator.getPlayerSellCost(item);
      if (event.isShiftClick()) {
        final int amount = avarionPlayer.getAmountInInventory(item);
        final ItemStack remover = item.clone();
        remover.setAmount(amount);
        inv.removeItem(remover);
        avarionPlayer.getEconomyAccount().add(basePrice * amount);
        CustomSound.COINS_SOUND.play(player, 0.665F, 1.2F);
        CustomSound.COINS_SOUND.play(player, 0.665F, 1.0F);
        CustomSound.COINS_SOUND.play(player, 0.665F, 0.8F);
        CustomSound.COINS_SOUND.play(player, 0.665F, 0.6F);
      } else if (event.isRightClick()) {
        final int amount = item.getAmount();
        inv.setItem(slot, null);
        avarionPlayer.getEconomyAccount().add(basePrice * amount);
        CustomSound.COINS_SOUND.play(player, 0.665F, 1F);
        CustomSound.COINS_SOUND.play(player, 0.665F, 0.8F);
      } else if (event.isLeftClick()) {
        final int amount = item.getAmount();
        if (amount == 1) {
          inv.setItem(slot, null);
        } else {
          item.setAmount(item.getAmount() - 1);
        }
        avarionPlayer.getEconomyAccount().add(basePrice);
        CustomSound.COINS_SOUND.play(player, 0.665F, 0.8F);
      }
    }

    event.setCancelled(true);
  }

}
