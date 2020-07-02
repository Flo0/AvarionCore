package com.gestankbratwurst.avarioncore.util;

import com.google.common.collect.MapMaker;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
public class OfflinePlayerCache {

  private static final ConcurrentMap<UUID, OfflinePlayer> nameCache = new MapMaker().weakKeys().makeMap();

  public static void load(UUID playerID) {
    if (nameCache.containsKey(playerID)) {
      return;
    }
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerID);
    nameCache.put(playerID, offlinePlayer);
  }

  public static void unload(UUID playerID) {
    nameCache.remove(playerID);
  }

  @Nullable
  public static OfflinePlayer get(UUID playerID) {
    Player player = Bukkit.getPlayer(playerID);
    return player == null ? nameCache.get(playerID) : player;
  }

  public static WeakReference<OfflinePlayer> getWeak(UUID playerID) {
    return new WeakReference<>(get(playerID));
  }

}
