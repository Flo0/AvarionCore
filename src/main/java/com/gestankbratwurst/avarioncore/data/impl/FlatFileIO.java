package com.gestankbratwurst.avarioncore.data.impl;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionIO;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
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

  public FlatFileIO(AvarionCore plugin) {
    File mainFolder = plugin.getDataFolder();
    if (!mainFolder.exists()) {
      mainFolder.mkdirs();
    }
    playerFolder = new File(mainFolder + File.separator + "playerdata");
    if (!playerFolder.exists()) {
      playerFolder.mkdirs();
    }
    worldFolder = new File(mainFolder + File.separator + "protectiondata");
    if (!worldFolder.exists()) {
      worldFolder.mkdirs();
    }
  }

  private final File playerFolder;
  private final File worldFolder;
  private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  @NotNull
  private Path getPlayerPath(UUID playerID) {
    return new File(playerFolder, playerID.toString() + ".json").toPath();
  }

  @NotNull
  private Path getWorldPath(UUID worldID) {
    return new File(worldFolder, worldID.toString() + ".json").toPath();
  }

  @Override
  public JsonObject loadPlayerData(@NotNull UUID playerID) {
    Path playerPath = getPlayerPath(playerID);
    if (Files.exists(playerPath)) {
      try {
        return gson.fromJson(Files.readString(playerPath), JsonObject.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  @Override
  public void savePlayerData(@NotNull UUID playerID, @NotNull JsonObject jsonData) {
    try {
      Files.writeString(getPlayerPath(playerID), gson.toJson(jsonData));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public @Nullable JsonObject loadWorldData(UUID worldID) {
    Path worldPath = getWorldPath(worldID);
    if (Files.exists(worldPath)) {
      try {
        return gson.fromJson(Files.readString(worldPath), JsonObject.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  @Override
  public void saveWorldData(UUID worldID, JsonObject jsonData) {
    try {
      Files.writeString(getWorldPath(worldID), gson.toJson(jsonData));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}