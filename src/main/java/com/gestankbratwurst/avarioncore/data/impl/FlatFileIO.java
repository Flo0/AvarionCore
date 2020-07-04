package com.gestankbratwurst.avarioncore.data.impl;

import co.aikar.commands.CommandCompletionContext;
import co.aikar.commands.CommandIssuer;
import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionIO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
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
public class FlatFileIO implements AvarionIO {

  public FlatFileIO(final AvarionCore plugin) {
    final File mainFolder = plugin.getDataFolder();
    if (!mainFolder.exists()) {
      mainFolder.mkdirs();
    }
    this.playerFolder = new File(mainFolder + File.separator + "playerdata");
    if (!this.playerFolder.exists()) {
      this.playerFolder.mkdirs();
    }
    this.worldFolder = new File(mainFolder + File.separator + "protectiondata");
    if (!this.worldFolder.exists()) {
      this.worldFolder.mkdirs();
    }
  }

  private final File playerFolder;
  private final File worldFolder;
  private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  @NotNull
  private Path getPlayerPath(final UUID playerID) {
    return new File(this.playerFolder, playerID.toString() + ".json").toPath();
  }

  @NotNull
  private Path getWorldPath(final UUID worldID) {
    return new File(this.worldFolder, worldID.toString() + ".json").toPath();
  }

  @Override
  public JsonObject loadPlayerData(@NotNull final UUID playerID) {
    final Path playerPath = this.getPlayerPath(playerID);
    if (Files.exists(playerPath)) {
      try {
        return this.gson.fromJson(Files.readString(playerPath), JsonObject.class);
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  @Override
  public void savePlayerData(@NotNull final UUID playerID, @NotNull final JsonObject jsonData) {
    try {
      Files.writeString(this.getPlayerPath(playerID), this.gson.toJson(jsonData));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public @Nullable JsonObject loadWorldData(final UUID worldID) {
    final Path worldPath = this.getWorldPath(worldID);
    if (Files.exists(worldPath)) {
      try {
        return this.gson.fromJson(Files.readString(worldPath), JsonObject.class);
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  @Override
  public void saveWorldData(final UUID worldID, final JsonObject jsonData) {
    try {
      Files.writeString(this.getWorldPath(worldID), this.gson.toJson(jsonData));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public <I extends CommandIssuer> List<String> getAvarionPlayerNames(final CommandCompletionContext<I> commandCompletionContext) {
    return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
  }

}