package com.gestankbratwurst.avarioncore.util;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.gestankbratwurst.avarioncore.util.common.UtilPlayer;
import com.gestankbratwurst.avarioncore.util.holograms.MovingHologram;
import com.gestankbratwurst.avarioncore.util.holograms.impl.HologramManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LaLaLand-CorePlugin and was created at the 17.11.2019
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@CommandAlias("test")
public class TestCommand extends BaseCommand {

  public TestCommand(final HologramManager holoManager) {
    this.holoManager = holoManager;
  }

  private final HologramManager holoManager;

  @Subcommand("holo")
  public void onHolo(final Player sender, final String line) {
    final MovingHologram hologram = holoManager.createMovingHologram(sender.getLocation(), new Vector(0, 0.1, 0), 60);
    hologram.getHologram().appendTextLine("§eTest");
  }

  @Subcommand("waiting")
  public void onWait(final Player sender, final int ticks) {
    UtilPlayer.forceWait(sender, ticks, true, pl -> pl.sendMessage("Wait is over."), pl -> pl.sendMessage("Wait cancel."));
  }


  @Subcommand("teleportup")
  public void testTeleport(Player sender, int ticks) {
    Location dest = sender.getLocation().add(0, 100, 0);
    UtilPlayer.teleport(sender, ticks, dest);
  }
}