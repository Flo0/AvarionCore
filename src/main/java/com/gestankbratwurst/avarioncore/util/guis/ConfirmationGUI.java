package com.gestankbratwurst.avarioncore.util.guis;

import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.gestankbratwurst.avarioncore.util.items.ItemBuilder;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import net.crytec.inventoryapi.SmartInventory;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.inventoryapi.api.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 26.04.2020
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@AllArgsConstructor
public class ConfirmationGUI implements InventoryProvider {

  public static void open(Player player, String question, Consumer<InventoryClickEvent> successConsumer,
      Consumer<InventoryClickEvent> failConsumer) {
    SmartInventory.builder().size(3).title(question).provider(new ConfirmationGUI(successConsumer, failConsumer)).build().open(player);
  }

  private final Consumer<InventoryClickEvent> successConsumer;
  private final Consumer<InventoryClickEvent> failConsumer;

  @Override
  public void init(Player player, InventoryContent content) {

    content.set(SlotPos.of(1, 2),
        ClickableItem.of(new ItemBuilder(Model.GREEN_CHECK.getItem()).name("§aBestätigen").build(), successConsumer));
    content.set(SlotPos.of(1, 6),
        ClickableItem.of(new ItemBuilder(Model.RED_X.getItem()).name("§cAblehnen").build(), failConsumer));

  }

}