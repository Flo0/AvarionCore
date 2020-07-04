package com.gestankbratwurst.avarioncore.commands.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Values;
import com.gestankbratwurst.avarioncore.data.FutureAvarionPlayer;
import com.gestankbratwurst.avarioncore.economy.EconomyAccount;
import com.gestankbratwurst.avarioncore.economy.MoneyItemHandler;
import com.gestankbratwurst.avarioncore.util.Msg;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 03.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@CommandAlias("money|geld")
@RequiredArgsConstructor
public class EconomyCommand extends BaseCommand {

  private final MoneyItemHandler moneyItemHandler;

  @Subcommand("drop")
  @CommandPermission("admin")
  public void onDrop(final Player player, final double amount) {
    final RayTraceResult rayTraceResult = player.rayTraceBlocks(12);
    if (rayTraceResult == null || rayTraceResult.getHitBlock() == null) {
      Msg.error(player, "Geld", "Du musst auf einen Block in der Näche schauen.");
      return;
    }
    final Block block = rayTraceResult.getHitBlock();
    final Block relative = block.getRelative(Objects.requireNonNull(rayTraceResult.getHitBlockFace()));
    final Location dropLoc = new Location(relative.getWorld(), relative.getX() + 0.5, relative.getY() + 0.5, relative.getZ() + 0.5);
    final Item item = this.moneyItemHandler.dropMoneyItem(dropLoc, amount);
    item.setCustomName("§a" + amount + EconomyAccount.MONEY_PLURAL);
    item.setCustomNameVisible(true);
  }

  @Subcommand("icon")
  @CommandPermission("admin")
  public void onIcon(final Player player, final double amount) {
    player.getInventory().addItem(this.moneyItemHandler.getIcon(amount));
    Msg.send(player, "Geld", "Geld icon erhalten.");
  }

  @Subcommand("admin add")
  @CommandPermission("admin")
  @CommandCompletion("@AvarionPlayerAsync <Menge>")
  public void onAdminAdd(final Player player, @Values("@AvarionPlayerAsync") final FutureAvarionPlayer futurePlayer, final double amount) {

    futurePlayer.thenAccept(avPlayer -> {
      avPlayer.getEconomyAccount().add(amount);
      avPlayer.sendMessage("Geld", "Du hast " + Msg.elem(amount + " " + EconomyAccount.MONEY_PLURAL) + " erhalten.", true);
    });

    Msg.send(player, "Geld", "Geld wurde gesendet.");
  }

}
