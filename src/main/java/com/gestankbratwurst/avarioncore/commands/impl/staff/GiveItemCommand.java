package com.gestankbratwurst.avarioncore.commands.impl.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.gestankbratwurst.avarioncore.data.FutureAvarionPlayer;
import com.gestankbratwurst.avarioncore.util.Msg;
import org.bukkit.Material;
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
@CommandAlias("giveitem")
@CommandPermission("admin")
public class GiveItemCommand extends BaseCommand {

  @Default
  @CommandCompletion("@AvarionPlayerAsync")
  public void onGive(final Player sender, final FutureAvarionPlayer target) {

    final ItemStack item = sender.getInventory().getItemInMainHand();

    if (item.getType() == Material.AIR) {
      Msg.error(sender, "Give", "Du musst ein item in der Hand halten.");
    }

    Msg.error(sender, "Give", "Du hast das item versendet.");
    target.thenAccept(avPlayer -> avPlayer.giveItem(item, false));
  }

}
