package com.gestankbratwurst.avarioncore.protection;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionIO;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.util.BoundingBox;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 30.06.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ProtectionManager implements Listener {

  public ProtectionManager(AvarionCore plugin) {
    this.worlds = new Object2ObjectOpenHashMap<>();
    this.avarionIO = plugin.getAvarionIO();

    for (World world : Bukkit.getWorlds()) {
      loadWorld(world.getUID());
    }

    Bukkit.getPluginManager().registerEvents(this, plugin);
    Bukkit.getPluginManager().registerEvents(new ProtectionListener(plugin, this), plugin);
  }

  private final AvarionIO avarionIO;
  private final Object2ObjectMap<UUID, WorldDomain> worlds;

  public WorldDomain getWorldDomain(UUID worldID) {
    return worlds.get(worldID);
  }

  public ProtectedRegion createRegion(Location corner1, Location corner2, UUID ownerID, int priority) {
    return createRegion(corner1.getBlock(), corner2.getBlock(), ownerID, priority);
  }

  public ProtectedRegion createRegion(Block corner1, Block corner2, UUID ownerID, int priority) {
    BoundingBox box = BoundingBox.of(corner1, corner2);
    ProtectedRegion region = new ProtectedRegion(box, ownerID, priority);
    worlds.get(corner1.getWorld().getUID()).addRegion(region);
    return region;
  }

  public ProtectedRegion getHighestPriorityRegionAt(Block block) {
    return worlds.get(block.getWorld().getUID()).getHighestPriorityRegionAt(block);
  }

  public RuleSet getPlayerRules(Block block, AvarionPlayer avarionPlayer) {
    return worlds.get(block.getWorld().getUID()).getPlayerRules(block, avarionPlayer);
  }

  public RuleSet getEnvironmentRules(Block block) {
    return worlds.get(block.getWorld().getUID()).getEnvironmentRules(block);
  }

  private void loadWorld(UUID worldID) {
    JsonObject data = avarionIO.loadWorldData(worldID);
    WorldDomain domain = data == null ? new WorldDomain(worldID) : new WorldDomain(data);
    worlds.put(worldID, domain);
  }

  private void unloadWorld(UUID worldID) {
    WorldDomain domain = worlds.get(worldID);
    if (domain != null) {
      avarionIO.saveWorldData(worldID, domain.getAsJson());
      worlds.remove(worldID);
    }
  }

  @EventHandler
  public void onLoad(WorldLoadEvent event) {
    loadWorld(event.getWorld().getUID());
  }

  @EventHandler
  public void onUnload(WorldUnloadEvent event) {
    unloadWorld(event.getWorld().getUID());
  }

  public void flushData() {
    for (UUID worldID : new HashSet<>(worlds.keySet())) {
      this.unloadWorld(worldID);
    }
  }

}