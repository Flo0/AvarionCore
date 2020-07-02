package com.gestankbratwurst.avarioncore.util.guis;

import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.gestankbratwurst.avarioncore.util.common.UtilPlayer;
import com.gestankbratwurst.avarioncore.util.items.ItemBuilder;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.crytec.inventoryapi.SmartInventory;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.inventoryapi.api.SlotPos;
import org.bukkit.Bukkit;
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
public class PlayerChooserGUI implements InventoryProvider {

  public static void open(Player player, String title, Consumer<Player> playerConsumer, Predicate<Player> playerFilter) {
    SmartInventory.builder().size(5).provider(new PlayerChooserGUI(playerConsumer, playerFilter)).title(title).build().open(player);
  }

  private PlayerChooserGUI(Consumer<Player> playerConsumer, Predicate<Player> playerFilter) {
    this.playerConsumer = playerConsumer;
    this.playerFilter = playerFilter;
  }

  private final Consumer<Player> playerConsumer;
  private final Predicate<Player> playerFilter;

  @Override
  public void init(Player player, InventoryContent content) {
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (playerFilter.test(onlinePlayer)) {
        ItemStack item = new ItemBuilder(UtilPlayer.getHead(onlinePlayer)).name("§e" + onlinePlayer.getName()).build();
        ClickableItem clickable = new ClickableItem(item, event -> playerConsumer.accept(onlinePlayer));
        content.add(clickable);
      }
    }

    ItemStack noneItem = new ItemBuilder(Model.RED_X.getItem()).name("§cKeiner").build();

    content.set(SlotPos.of(4, 8), ClickableItem.of(noneItem, event -> playerConsumer.accept(null)));
  }

}
