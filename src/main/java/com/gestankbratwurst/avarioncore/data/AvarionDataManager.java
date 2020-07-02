package com.gestankbratwurst.avarioncore.data;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
public class AvarionDataManager implements Listener, AsyncCacheLoader<UUID, AvarionPlayer>, RemovalListener<UUID, AvarionPlayer> {

  public AvarionDataManager(AvarionCore avarionCore) {
    Bukkit.getPluginManager().registerEvents(this, avarionCore);
    this.avarionIO = avarionCore.getAvarionIO();
    this.onlinePlayerMap = new Object2ObjectOpenHashMap<>();
    this.offlinePlayerCache = Caffeine.newBuilder()
        .expireAfterAccess(30, TimeUnit.MINUTES)
        .removalListener(this)
        .buildAsync(this);
  }

  private final AvarionIO avarionIO;
  private final Map<UUID, AvarionPlayer> onlinePlayerMap;
  private final AsyncLoadingCache<UUID, AvarionPlayer> offlinePlayerCache;

  public long getCacheOverhead() {
    return offlinePlayerCache.synchronous().estimatedSize() - onlinePlayerMap.size();
  }

  public CacheStats getOfflinePlayerCacheStats() {
    return offlinePlayerCache.synchronous().stats();
  }

  public void flushData() {
    offlinePlayerCache.synchronous().asMap().values().forEach(avarionIO::savePlayer);
  }

  @Nullable
  public AvarionPlayer getOnlineData(UUID playerID) {
    return onlinePlayerMap.get(playerID);
  }

  @NotNull
  public CompletableFuture<AvarionPlayer> getData(UUID playerID) {
    return offlinePlayerCache.get(playerID);
  }

  @Override
  @NotNull
  public CompletableFuture<AvarionPlayer> asyncLoad(@NotNull UUID key, @NotNull Executor executor) {
    if (onlinePlayerMap.containsKey(key)) {
      return CompletableFuture.supplyAsync(() -> onlinePlayerMap.get(key), executor);
    }
    return avarionIO.loadPlayerAsync(key, executor);
  }

  @Override
  public void onRemoval(@Nullable UUID key, @Nullable AvarionPlayer value, @NotNull RemovalCause cause) {
    if (key != null && value != null) {
      avarionIO.savePlayerAsync(value);
    }
  }

  @EventHandler
  public void preLogin(AsyncPlayerPreLoginEvent event) {
    UUID playerID = event.getUniqueId();

    try {
      AvarionPlayer avPlayer = offlinePlayerCache.get(playerID).get();
      onlinePlayerMap.put(playerID, avPlayer);
    } catch (InterruptedException | ExecutionException e) {
      Bukkit.getLogger().severe("Error while player was logging in.");
      e.printStackTrace();
      event.setKickMessage("§cError: §fSpielerdaten konnten nicht geladen werden.");
      event.setLoginResult(Result.KICK_OTHER);
      return;
    }

    event.setLoginResult(Result.ALLOWED);
  }

  @EventHandler
  public void onLogin(PlayerLoginEvent event) {
    onlinePlayerMap.get(event.getPlayer().getUniqueId()).onLogin(event);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    UUID playerID = event.getPlayer().getUniqueId();
    offlinePlayerCache.get(playerID);
    onlinePlayerMap.remove(playerID);
  }

}
