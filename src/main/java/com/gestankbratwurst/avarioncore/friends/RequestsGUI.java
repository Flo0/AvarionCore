package com.gestankbratwurst.avarioncore.friends;

import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.gestankbratwurst.avarioncore.util.OfflinePlayerCache;
import com.gestankbratwurst.avarioncore.util.common.UtilPlayer;
import com.gestankbratwurst.avarioncore.util.items.ItemBuilder;
import net.crytec.inventoryapi.SmartInventory;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.inventoryapi.api.SlotPos;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 01.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class RequestsGUI implements InventoryProvider {

  public static void open(AvarionPlayer avarionPlayer) {
    SmartInventory.builder().title("Anfragen").size(5).provider(new RequestsGUI(avarionPlayer)).build().open(avarionPlayer.getPlayer());
  }

  private RequestsGUI(AvarionPlayer avarionPlayer) {
    this.avarionPlayer = avarionPlayer;
  }

  private final AvarionPlayer avarionPlayer;

  @Override
  public void init(Player player, InventoryContent content) {
    for (FriendRequest request : avarionPlayer.getFriendAccount().listReceivedRequests()) {
      ItemStack item = new ItemBuilder(UtilPlayer.getHead(OfflinePlayerCache.get(request.getInquirerID())))
          .name("§e" + request.getInquirerName())
          .lore("")
          .lore("§fLinksklick: §aAnnehmen §f| Rechtsklick: §cAblehnen")
          .build();
      content.add(ClickableItem.of(item, event -> {
        if (event.isLeftClick()) {
          request.accept(avarionPlayer);
          UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK, 0.7F, 1.2F);
        } else if (event.isRightClick()) {
          request.decline(avarionPlayer);
          UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK, 0.7F, 1.2F);
        }
        reopen(player, content);
      }));
    }

    ItemStack backItem = new ItemBuilder(Model.BLACK_ARROW_LEFT.getItem()).name("§fZurück").build();
    content.set(SlotPos.of(4, 0), ClickableItem.of(backItem, event -> {
      UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK, 0.7F, 1.2F);
      FriendsMainGUI.open(avarionPlayer);
    }));
  }

  @Override
  public void onClose(Player player, InventoryContent content) {

  }

}
