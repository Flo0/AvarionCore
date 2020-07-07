package com.gestankbratwurst.avarioncore.economy.adminshops.guis;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionDataManager;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.economy.EconomyAccount;
import com.gestankbratwurst.avarioncore.economy.abstraction.Tradable;
import com.gestankbratwurst.avarioncore.economy.adminshops.AdminShop;
import com.gestankbratwurst.avarioncore.util.Msg;
import com.gestankbratwurst.avarioncore.util.common.UtilPlayer;
import java.util.List;
import java.util.Objects;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.inventoryapi.api.Pagination;
import net.crytec.inventoryapi.api.SlotIterator;
import net.crytec.inventoryapi.api.SlotIterator.Type;
import net.crytec.inventoryapi.api.SlotPos;
import org.bukkit.Sound;
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
    this.avarionDataManager = AvarionCore.getInstance().getAvarionDataManager();
  }

  private final AdminShop adminShop;
  private final AvarionDataManager avarionDataManager;

  @Override
  public void init(final Player player, final InventoryContent content) {
    final AvarionPlayer avPlayer = AvarionCore.getInstance().getAvarionDataManager().getOnlineData(player.getUniqueId());
    Objects.requireNonNull(avPlayer);

    final Pagination pagination = content.pagination();
    pagination.setItemsPerPage(28);

    final List<Tradable> tradableList = this.adminShop.getTradables();
    final ClickableItem[] clickableItems = new ClickableItem[tradableList.size()];
    int index = 0;
    for (final Tradable tradable : tradableList) {
      final ItemStack representation = tradable.getRepresentation(avPlayer);
      clickableItems[index++] = ClickableItem.of(representation, event -> {
        final int amount;
        if (event.isShiftClick()) {
          amount = representation.getMaxStackSize();
        } else if (event.isRightClick()) {
          amount = representation.getMaxStackSize() / 2;
        } else if (event.isLeftClick()) {
          amount = 1;
        } else {
          return;
        }
        final double cost = tradable.getCostToBuy(avPlayer) * amount;
        final EconomyAccount account = avPlayer.getEconomyAccount();
        if (account.getBalance() < cost) {
          Msg.error(player, "Shops", "Du hast nicht genügend Geld.");
          return;
        }
        account.remove(cost);
        tradable.giveTo(avPlayer, amount);
        UtilPlayer.playSound(player, Sound.ENTITY_ITEM_PICKUP, 0.8F, 0.8F);
        Msg.error(player, "Shops", "Item wurde gekauft.");
      });
    }
    pagination.setItems(clickableItems);

    final SlotIterator iterator = content.newIterator(Type.HORIZONTAL, SlotPos.of(1, 1));
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 6; j++) {
        if (i < 1 || i > 7 || j < 1 || j > 4) {
          iterator.blacklist(SlotPos.of(j, i));
        }
      }
    }
    iterator.addPagination(pagination);

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

    event.setCancelled(true);
  }

}
