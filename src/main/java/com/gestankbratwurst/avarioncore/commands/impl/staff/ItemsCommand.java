package com.gestankbratwurst.avarioncore.commands.impl.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import com.gestankbratwurst.avarioncore.data.FutureAvarionPlayer;
import com.gestankbratwurst.avarioncore.util.Msg;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 06.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@CommandAlias("items")
@CommandPermission("admin")
public class ItemsCommand extends BaseCommand {

  @Subcommand("colorthis")
  public void onCol(final Player player, final String msg) {
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
  }

  @Subcommand("give")
  @CommandCompletion("@AvarionPlayerAsync")
  @Conditions("ItemInHand")
  public void onGive(final Player sender, final FutureAvarionPlayer target) {

    final ItemStack item = sender.getInventory().getItemInMainHand();

    Msg.error(sender, "Items", "Du hast das item versendet.");
    target.thenAccept(avPlayer -> avPlayer.giveItem(item, false));
  }

  @Subcommand("has")
  @CommandCompletion("@AvarionPlayerAsync")
  @Conditions("ItemInHand")
  public void onHas(final Player sender, final FutureAvarionPlayer target) {

    final ItemStack item = sender.getInventory().getItemInMainHand();

    target.thenAccept(avPlayer -> {
      if (avPlayer.hasItem(item)) {
        Msg.send(sender, "Items", "Der Spieler hat dieses Item in dieser Menge.");
      } else {
        Msg.send(sender, "Items", "Der Spieler hat dieses Item in dieser Menge nicht!");
      }
    });
  }

  @Subcommand("remove")
  @CommandCompletion("@AvarionPlayerAsync")
  @Conditions("ItemInHand")
  public void onRemove(final Player sender, final FutureAvarionPlayer target) {

    final ItemStack item = sender.getInventory().getItemInMainHand();

    Msg.send(sender, "Items", "Items wurden entfernt.");
    target.thenAccept(avPlayer -> avPlayer.removeItem(item));
  }

}
