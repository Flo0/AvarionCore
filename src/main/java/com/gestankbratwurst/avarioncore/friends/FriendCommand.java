package com.gestankbratwurst.avarioncore.friends;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.gestankbratwurst.avarioncore.AvarionCore;
import lombok.AllArgsConstructor;
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
@CommandAlias("freunde|friends")
@AllArgsConstructor
public class FriendCommand extends BaseCommand {

  private final AvarionCore plugin;

  @Default
  public void onDefault(Player sender) {
    plugin.getAvarionDataManager().getOnlineData(sender.getUniqueId()).openFriendGUI();
  }

}
