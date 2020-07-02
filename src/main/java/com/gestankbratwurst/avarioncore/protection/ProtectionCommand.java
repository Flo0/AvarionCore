package com.gestankbratwurst.avarioncore.protection;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Values;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 01.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@CommandAlias("protection")
@CommandPermission("admin")
@RequiredArgsConstructor
public class ProtectionCommand extends BaseCommand {

  private final ProtectionManager protectionManager;
  private final Map<UUID, Location[]> POS_ = new HashMap<>();

  @Subcommand("worldrule")
  @CommandCompletion("@ProtectionRule @RuleState")
  public void onWorldRule(Player player, @Values("@ProtectionRule") ProtectionRule rule, @Values("@RuleState") RuleState state) {
    WorldDomain worldDomain = protectionManager.getWorldDomain(player.getWorld().getUID());
    if (!rule.isGlobalContext()) {
      worldDomain.setWorldPlayerRule(rule, state);
    } else {
      worldDomain.setWorldGlobalRule(rule, state);
    }
    player.sendMessage("Rule is set");
  }

  @Subcommand("pos1")
  public void onPosOne(Player player) {
    if (POS_.containsKey(player.getUniqueId())) {
      POS_.get(player.getUniqueId())[0] = player.getLocation();
    } else {
      POS_.put(player.getUniqueId(), new Location[]{player.getLocation(), null});
    }
    player.sendMessage("POS 1");
  }

  @Subcommand("pos2")
  public void onPosTwo(Player player) {
    if (POS_.containsKey(player.getUniqueId())) {
      POS_.get(player.getUniqueId())[1] = player.getLocation();
    } else {
      POS_.put(player.getUniqueId(), new Location[]{null, player.getLocation()});
    }
    player.sendMessage("POS 2");
  }

  @Subcommand("create")
  public void onCreate(Player player, int prio, String name) {
    if (!POS_.containsKey(player.getUniqueId())) {
      player.sendMessage("Pos setzen...");
      return;
    }
    Location[] locations = POS_.get(player.getUniqueId());
    if (locations[0] == null || locations[1] == null) {
      player.sendMessage("Pos setzen...");
      return;
    }

    protectionManager.createRegion(locations[0], locations[1], player.getUniqueId(), prio).setRegionName(name);

    player.sendMessage("Region created.");
  }

  @Subcommand("info")
  public void onInfo(Player player) {
    Block block = player.getLocation().getBlock();

    ProtectedRegion region = protectionManager.getHighestPriorityRegionAt(block);

    if (region == null) {
      player.sendMessage("No region here.");
    } else {
      player.sendMessage("Region: " + region.getRegionName());
    }
  }

}
