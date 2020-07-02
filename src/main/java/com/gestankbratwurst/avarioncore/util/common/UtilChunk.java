package com.gestankbratwurst.avarioncore.util.common;

import com.gestankbratwurst.avarioncore.util.events.PlayerChangeChunkEvent;
import com.gestankbratwurst.avarioncore.util.events.PlayerReceiveChunkEvent;
import com.gestankbratwurst.avarioncore.util.events.PlayerUnloadsChunkEvent;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LaLaLand-CorePlugin and was created at the 17.11.2019
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class UtilChunk implements Listener {

  private static UtilChunk instance;
  private static boolean initialized = false;
  private static final Object2ObjectOpenHashMap<Player, Set<Long>> CHUNK_VIEWS = new Object2ObjectOpenHashMap<Player, Set<Long>>();

  public static void init(JavaPlugin host) {
    Preconditions.checkArgument(!initialized, "UtilChunk is already initialized!");
    instance = new UtilChunk();
    Bukkit.getPluginManager().registerEvents(instance, host);
    Bukkit.getOnlinePlayers()
        .forEach(player -> CHUNK_VIEWS.put(player, Sets.newHashSet())); // Handle reloads
    initialized = true;
  }

  private UtilChunk() {
  }

  @EventHandler
  public void onChunkReceive(PlayerReceiveChunkEvent event) {
    CHUNK_VIEWS.get(event.getPlayer()).add(event.getChunkKey());
  }

  @EventHandler
  public void onChunkReceive(PlayerUnloadsChunkEvent event) {
    CHUNK_VIEWS.get(event.getPlayer()).remove(event.getChunkKey());
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onJoin(PlayerJoinEvent event) {
    CHUNK_VIEWS.put(event.getPlayer(), Sets.newHashSet());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    CHUNK_VIEWS.remove(event.getPlayer());
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    Location from = event.getFrom();
    Location to = event.getTo();
    if (getChunkKey(from) != getChunkKey(to)) {
      new PlayerChangeChunkEvent(event.getPlayer(), from.getChunk(), to.getChunk()).callEvent();
    }
  }

  public static int[] getChunkCoords(long chunkKey) {
    int x = ((int) chunkKey) >> 32;
    int z = (int) (chunkKey >> 32) >> 32;
    return new int[]{x, z};
  }

  public static long getChunkKey(int x, int z) {
    return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
  }

  public static long getChunkKey(Chunk chunk) {
    return (long) chunk.getX() & 0xffffffffL | ((long) chunk.getZ() & 0xffffffffL) << 32;
  }

  public static Chunk keyToChunk(World world, long chunkID) {
    Preconditions.checkArgument(world != null, "World cannot be null");
    return world.getChunkAt((int) chunkID, (int) (chunkID >> 32));
  }

  public static boolean isChunkLoaded(Location loc) {
    int chunkX = loc.getBlockX() >> 4;
    int chunkZ = loc.getBlockZ() >> 4;
    return loc.getWorld().isChunkLoaded(chunkX, chunkZ);
  }

  public static long getChunkKey(Location loc) {
    return getChunkKey(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
  }

  public static long getChunkKey(ChunkSnapshot chunk) {
    return (long) chunk.getX() & 0xffffffffL | ((long) chunk.getZ() & 0xffffffffL) << 32;
  }

  public static Set<Long> getChunkViews(Player player) {
    return CHUNK_VIEWS.get(player);
  }

  public static boolean isChunkInView(Player player, Chunk chunk) {
    return CHUNK_VIEWS.get(player).contains(getChunkKey(chunk));
  }

}
