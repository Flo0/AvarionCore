package com.gestankbratwurst.avarioncore.data;

import com.google.gson.JsonObject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.jetbrains.annotations.NotNull;
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
    JsonObject data = loadPlayerData(playerID);
    if (data == null) {
      return new AvarionPlayer(playerID);
    } else {
      return new AvarionPlayer(data);
    }
  }

  default void savePlayer(final AvarionPlayer avarionPlayer) {
    savePlayerData(avarionPlayer.getPlayerID(), avarionPlayer.getAsJson());
  }

  default CompletableFuture<AvarionPlayer> loadPlayerAsync(final UUID playerID, final Executor executor) {
    return CompletableFuture.supplyAsync(() -> loadPlayer(playerID), executor);
  }

  default CompletableFuture<Void> savePlayerAsync(AvarionPlayer avarionPlayer, final Executor executor) {
    return CompletableFuture.runAsync(() -> savePlayer(avarionPlayer), executor);
  }

  default CompletableFuture<Void> savePlayerAsync(AvarionPlayer avarionPlayer) {
    return CompletableFuture.runAsync(() -> savePlayer(avarionPlayer));
  }

}
