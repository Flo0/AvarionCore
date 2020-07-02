package com.gestankbratwurst.avarioncore.util.common;


import com.google.common.base.Preconditions;
import java.util.Set;
import net.crytec.libs.protocol.tracking.ChunkTracker;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LaLaLand-CorePlugin and was created at the 17.11.2019
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class UtilChunk {

  public static int[] getChunkCoords(long chunkKey) {
    int x = ((int) chunkKey);
    int z = (int) (chunkKey >> 32);
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
    return ChunkTracker.getChunkViews(player);
  }

  public static boolean isChunkInView(Player player, Chunk chunk) {
    return ChunkTracker.getChunkViews(player).contains(chunk.getChunkKey());
  }

}
