package com.gestankbratwurst.avarioncore.commands;

import co.aikar.commands.PaperCommandManager;
import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.commands.impl.EconomyCommand;
import com.gestankbratwurst.avarioncore.commands.impl.FriendCommand;
import com.gestankbratwurst.avarioncore.commands.impl.ProtectionCommand;
import com.gestankbratwurst.avarioncore.data.FutureAvarionPlayer;
import com.gestankbratwurst.avarioncore.protection.ProtectionRule;
import com.gestankbratwurst.avarioncore.protection.RuleState;
import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.gestankbratwurst.avarioncore.resourcepack.skins.ModelItemCommand;
import com.gestankbratwurst.avarioncore.resourcepack.sounds.CustomSound;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import net.minecraft.server.v1_16_R1.SoundCategory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 04.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class AvarionCommandManager {

  public AvarionCommandManager(final AvarionCore avarionCore) {
    this.avarionCore = avarionCore;
    this.commandManager = new PaperCommandManager(avarionCore);
  }

  private final AvarionCore avarionCore;
  private final PaperCommandManager commandManager;

  public void registerCompletions() {

    this.commandManager.getCommandCompletions().registerStaticCompletion("ModelItem",
        ImmutableList.copyOf(Arrays.stream(Model.values()).map(Enum::toString).collect(Collectors.toList())));

    this.commandManager.getCommandCompletions().registerStaticCompletion("CustomSound",
        ImmutableList.copyOf(Arrays.stream(CustomSound.values()).map(Enum::toString).collect(Collectors.toList())));

    this.commandManager.getCommandCompletions().registerStaticCompletion("SoundCategory",
        ImmutableList.copyOf(Arrays.stream(SoundCategory.values()).map(Enum::toString).collect(Collectors.toList())));

    this.commandManager.getCommandCompletions().registerStaticCompletion("ProtectionRule", Arrays
        .stream(ProtectionRule.values())
        .map(Enum::toString)
        .collect(Collectors.toList()));

    this.commandManager.getCommandCompletions().registerStaticCompletion("RuleState", Arrays
        .stream(RuleState.values())
        .map(Enum::toString)
        .collect(Collectors.toList()));

    this.commandManager.getCommandCompletions()
        .registerAsyncCompletion("AvarionPlayerAsync", c -> this.avarionCore.getAvarionIO().getAvarionPlayerNames(c));

  }

  public void registerContextResolver() {
    this.commandManager.getCommandContexts().registerContext(FutureAvarionPlayer.class, context -> {

      final FutureAvarionPlayer futurePlayer = new FutureAvarionPlayer();
      final String name = context.popFirstArg();

      CompletableFuture.runAsync(() -> {
        final Player player = Bukkit.getPlayer(name);
        if (player != null) {
          futurePlayer.complete(this.avarionCore.getAvarionDataManager().getOnlineData(player.getUniqueId()));
        } else {
          final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
          if (!offlinePlayer.hasPlayedBefore()) {
            throw new IllegalArgumentException("No offline player in database? -> " + offlinePlayer.getName());
          }
          try {
            futurePlayer.complete(this.avarionCore.getAvarionDataManager().getData(offlinePlayer.getUniqueId()).get());
          } catch (final InterruptedException | ExecutionException e) {
            e.printStackTrace();
          }
        }
      });

      return futurePlayer;
    });
  }

  public void registerCommands() {
    this.commandManager.registerCommand(new ProtectionCommand(this.avarionCore.getProtectionManager()));
    this.commandManager.registerCommand(new FriendCommand(this.avarionCore));
    this.commandManager.registerCommand(new EconomyCommand(this.avarionCore.getMoneyItemHandler()));
    this.commandManager.registerCommand(new ModelItemCommand());
  }

}
