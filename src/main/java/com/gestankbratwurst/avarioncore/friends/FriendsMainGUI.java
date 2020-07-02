package com.gestankbratwurst.avarioncore.friends;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.gestankbratwurst.avarioncore.util.OfflinePlayerCache;
import com.gestankbratwurst.avarioncore.util.common.UtilPlayer;
import com.gestankbratwurst.avarioncore.util.guis.PlayerChooserGUI;
import com.gestankbratwurst.avarioncore.util.items.ItemBuilder;
import java.util.UUID;
import net.crytec.inventoryapi.SmartInventory;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.inventoryapi.api.SlotPos;
import org.bukkit.OfflinePlayer;
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
public class FriendsMainGUI implements InventoryProvider {

  public static void open(AvarionPlayer avPlayer) {
    SmartInventory.builder().size(5).title("Freunde").provider(new FriendsMainGUI(avPlayer)).build().open(avPlayer.getPlayer());
  }

  private FriendsMainGUI(AvarionPlayer avPlayer) {
    this.avPlayer = avPlayer;
  }

  private final AvarionPlayer avPlayer;

  @Override
  public void init(Player player, InventoryContent content) {
    for (UUID friendID : avPlayer.getFriendAccount().listFriends()) {
      OfflinePlayer offlinePlayer = OfflinePlayerCache.get(friendID);
      if (offlinePlayer == null) {
        throw new IllegalStateException("OfflinePlayer not in cache.");
      }
      ItemStack head = new ItemBuilder(UtilPlayer.getHead(offlinePlayer)).name("§e" + offlinePlayer.getName()).build();
      content.add(ClickableItem.of(head, event -> {
        // TODO friend actions
      }));
    }

    ItemStack addFriendItem = new ItemBuilder(Model.GREEN_PLUS.getItem()).name("§aFreundschaftsanfrage senden").build();

    content.set(SlotPos.of(4, 8), ClickableItem.of(addFriendItem, event -> PlayerChooserGUI.open(player, "Spieler wählen", pl -> {

      if (pl != null) {
        AvarionPlayer target = AvarionCore.getInstance().getAvarionDataManager().getOnlineData(pl.getUniqueId());
        avPlayer.getFriendAccount().sendRequest(target);
      }

      reopen(player, content);
      UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK, 0.7F, 1.2F);
    }, pl -> !avPlayer.getFriendAccount().isFriend(pl.getUniqueId()) && !pl.equals(player))));

    ItemStack requestItem = new ItemBuilder(Model.SCROLL.getItem())
        .name("§aFreundschaftsanfragen an dich: §f" + avPlayer.getFriendAccount().getRequestCount()).build();

    content.set(SlotPos.of(4, 7), ClickableItem.of(requestItem, event -> {
      UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK, 0.7F, 1.2F);
      RequestsGUI.open(avPlayer);
    }));

  }

}
