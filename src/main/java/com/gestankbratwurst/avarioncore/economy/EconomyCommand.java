package com.gestankbratwurst.avarioncore.economy;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
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
  public void onSpawn(Player player, double amount, boolean name) {
    RayTraceResult rayTraceResult = player.rayTraceBlocks(12);
    if (rayTraceResult == null || rayTraceResult.getHitBlock() == null) {
      Msg.error(player, "Geld", "Du musst auf einen Block in der Näche schauen.");
      return;
    }
    Block block = rayTraceResult.getHitBlock();
    Block relative = block.getRelative(Objects.requireNonNull(rayTraceResult.getHitBlockFace()));
    Location dropLoc = new Location(relative.getWorld(), relative.getX() + 0.5, relative.getY() + 0.5, relative.getZ() + 0.5);
    Item item = moneyItemHandler.dropMoneyItem(dropLoc, amount);
    item.setCustomName("§a" + amount + EconomyAccount.MONEY_PLURAL);
    item.setCustomNameVisible(name);
  }

}
