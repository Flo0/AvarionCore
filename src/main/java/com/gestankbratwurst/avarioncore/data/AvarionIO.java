package com.gestankbratwurst.avarioncore.data;

import co.aikar.commands.CommandCompletionContext;
import co.aikar.commands.CommandIssuer;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.jetbrains.annotations.Nullable;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 30.06.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public interface AvarionIO {

  @Nullable
  JsonObject loadPlayerData(final UUID playerID);

  void savePlayerData(final UUID playerID, final JsonObject jsonData);

  @Nullable
  JsonObject loadWorldData(final UUID worldID);

  void saveWorldData(final UUID worldID, final JsonObject jsonData);

  default AvarionPlayer loadPlayer(final UUID playerID) {
    final JsonObject data = this.loadPlayerData(playerID);
    if (data == null) {
      return new AvarionPlayer(playerID);
    } else {
      return new AvarionPlayer(data);
    }
  }

  <I extends CommandIssuer> List<String> getAvarionPlayerNames(CommandCompletionContext<I> commandCompletionContext);

  default void savePlayer(final AvarionPlayer avarionPlayer) {
    this.savePlayerData(avarionPlayer.getPlayerID(), avarionPlayer.getAsJson());
  }

  default CompletableFuture<AvarionPlayer> loadPlayerAsync(final UUID playerID, final Executor executor) {
    return CompletableFuture.supplyAsync(() -> this.loadPlayer(playerID), executor);
  }

  default CompletableFuture<Void> savePlayerAsync(final AvarionPlayer avarionPlayer, final Executor executor) {
    return CompletableFuture.runAsync(() -> this.savePlayer(avarionPlayer), executor);
  }

  default CompletableFuture<Void> savePlayerAsync(final AvarionPlayer avarionPlayer) {
    return CompletableFuture.runAsync(() -> this.savePlayer(avarionPlayer));
  }

}
