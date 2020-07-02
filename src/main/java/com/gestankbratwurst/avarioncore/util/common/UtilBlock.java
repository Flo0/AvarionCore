package com.gestankbratwurst.avarioncore.util.common;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.tasks.TaskManager;
import com.gestankbratwurst.avarioncore.util.common.sub.BlockPosition;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 26.03.2020
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class UtilBlock implements Listener {

  private static UtilBlock listenerInstance;

  private static final Map<World, Long2ObjectOpenHashMap<Set<BlockPosition>>> PLAYER_PLACED_BLOCKS = new Object2ObjectOpenHashMap<>();

  public static void terminate(AvarionCore plugin) {
    for (World world : Bukkit.getWorlds()) {
      for (Chunk chunk : world.getLoadedChunks()) {
        listenerInstance.terminateChunk(chunk);
      }
      listenerInstance.terminateWorld(world);
    }
  }

  public static void init(AvarionCore plugin) {
    listenerInstance = new UtilBlock(plugin);
    Bukkit.getPluginManager().registerEvents(listenerInstance, plugin);
  }

  public static boolean isPlayerPlaced(Block block) {
    Long2ObjectMap<Set<BlockPosition>> chunkMap = PLAYER_PLACED_BLOCKS.get(block.getWorld());
    if (chunkMap == null) {
      return false;
    }
    Set<BlockPosition> positionSet = chunkMap.get(UtilChunk.getChunkKey(block.getLocation()));
    if (positionSet == null) {
      return false;
    }
    return positionSet.contains(new BlockPosition(block));
  }

  public static boolean isPlayerPlaced(Location location) {
    return isPlayerPlaced(location.getBlock());
  }

  private UtilBlock(AvarionCore plugin) {
    this.plugin = plugin;
    dataFolder = new File(plugin.getDataFolder() + File.separator + "playerblockcache");
    worldFolderMap = new ConcurrentHashMap<>();
    taskManager = AvarionCore.getInstance().getTaskManager();
    if (!dataFolder.exists()) {
      dataFolder.mkdirs();
    }
    for (World world : Bukkit.getWorlds()) {
      initWorld(world);
      for (Chunk chunk : world.getLoadedChunks()) {
        initChunk(chunk);
      }
    }
  }

  private final AvarionCore plugin;
  private final File dataFolder;
  private final ConcurrentHashMap<World, File> worldFolderMap;
  private final TaskManager taskManager;

  private void initWorld(World world) {
    Long2ObjectOpenHashMap chunkMap = new Long2ObjectOpenHashMap<Set<BlockPosition>>();
    PLAYER_PLACED_BLOCKS.put(world, chunkMap);
    CompletableFuture.supplyAsync(() -> new File(dataFolder + File.separator + world.getUID().toString()))
        .thenAccept(folder -> {
          if (!folder.exists()) {
            folder.mkdirs();
          }
          worldFolderMap.put(world, folder);
        }).thenRun(() -> taskManager.runBukkitSync(() -> {
      for (Chunk chunk : world.getLoadedChunks()) {
        initChunk(chunk);
      }
    }));
  }

  private void terminateWorld(World world) {
    for (Chunk chunk : world.getLoadedChunks()) {
      terminateChunk(chunk);
    }
    PLAYER_PLACED_BLOCKS.remove(world);
  }

  private void initChunk(Chunk chunk) {
    World world = chunk.getWorld();
    long chunkKey = UtilChunk.getChunkKey(chunk);
    if (PLAYER_PLACED_BLOCKS.get(world).containsKey(chunkKey)) {
      return;
    }

    CompletableFuture.supplyAsync(() -> new File(worldFolderMap.get(world), chunkKey + ".cpbd"))
        .thenApply(file -> {
          ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
          buffer.putInt(0);
          if (file.exists()) {
            try {
              FileInputStream inputStream = new FileInputStream(file);
              buffer = ByteBuffer.wrap(inputStream.readAllBytes());
              inputStream.close();
              return buffer;
            } catch (IOException e) {
              return buffer;
            }
          }
          return buffer;
        }).thenAccept(buffer -> {
      buffer.rewind();
      final Set<BlockPosition> positionSet = new ObjectOpenHashSet<>();
      if (buffer.hasRemaining()) {
        int lenght = buffer.getInt();
        for (int index = 0; index < lenght; index++) {
          positionSet.add(new BlockPosition(buffer.getInt(), buffer.getInt(), buffer.getInt()));
        }
      }
      taskManager.runBukkitSync(() -> PLAYER_PLACED_BLOCKS.get(world).put(chunkKey, positionSet));
    });
  }

  private void terminateChunk(Chunk chunk) {
    final World world = chunk.getWorld();
    final long chunkKey = UtilChunk.getChunkKey(chunk);
    final Set<BlockPosition> positions = PLAYER_PLACED_BLOCKS.get(world).remove(chunkKey);
    if (positions == null) {
      return;
    }
    final int size = positions.size();
    CompletableFuture.supplyAsync(() -> new File(worldFolderMap.get(world), chunkKey + ".cpbd"))
        .thenAccept(file -> {
          if (size == 0) {
            if (file.exists()) {
              file.delete();
            }
            return;
          }
          try {
            FileOutputStream fos = new FileOutputStream(file);
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 3 * size + Integer.BYTES);
            buffer.putInt(size);
            for (BlockPosition position : positions) {
              buffer.putInt(position.x);
              buffer.putInt(position.y);
              buffer.putInt(position.z);
            }
            fos.write(buffer.array());
            fos.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  private void initBlock(Block block) {
    Chunk chunk = block.getChunk();
    long chunkKey = UtilChunk.getChunkKey(chunk);
    Long2ObjectMap<Set<BlockPosition>> chunkMap = PLAYER_PLACED_BLOCKS.get(block.getWorld());
    if (chunkMap == null) {
      System.out.println("World not init.");
    }
    Set<BlockPosition> positionSet = chunkMap.get(chunkKey);
    BlockPosition position = new BlockPosition(block);
    if (positionSet == null) {
      plugin.getLogger().warning("Player Block Tracker compomised!");
      plugin.getLogger().warning("@ " + position.toString());
      return;
    }
    positionSet.add(position);
  }

  private void termintateBlock(Block block) {
    Long2ObjectMap<Set<BlockPosition>> chunkMap = PLAYER_PLACED_BLOCKS.get(block.getWorld());
    if (chunkMap == null) {
      return;
    }
    long chunkKey = UtilChunk.getChunkKey(block.getLocation());
    Set<BlockPosition> positionSet = chunkMap.get(chunkKey);
    if (positionSet == null) {
      return;
    }
    positionSet.remove(new BlockPosition(block));
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onWorldLoad(WorldLoadEvent event) {
    initWorld(event.getWorld());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onWorldUnload(WorldUnloadEvent event) {
    terminateWorld(event.getWorld());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChunkLoad(ChunkLoadEvent event) {
    initChunk(event.getChunk());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onChunkUnload(ChunkUnloadEvent event) {
    terminateChunk(event.getChunk());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPistonMove(BlockPistonExtendEvent event) {
    if (event.isCancelled()) {
      return;
    }
    List<Block> blocks = event.getBlocks();
    if (blocks.size() > 0) {
      BlockFace dir = event.getDirection();
      termintateBlock(blocks.get(0));
      initBlock(blocks.get(blocks.size() - 1).getRelative(dir));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPistonMove(BlockPistonRetractEvent event) {
    if (event.isCancelled()) {
      return;
    }
    List<Block> blocks = event.getBlocks();
    if (blocks.size() > 0) {
      BlockFace dir = event.getDirection();
      termintateBlock(blocks.get(0));
      initBlock(blocks.get(blocks.size() - 1).getRelative(dir));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockPlaced(BlockPlaceEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
      return;
    }
    initBlock(event.getBlock());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBreak(BlockBreakEvent event) {
    if (event.isCancelled()) {
      return;
    }
    termintateBlock(event.getBlock());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBurn(BlockBurnEvent event) {
    if (event.isCancelled()) {
      return;
    }
    termintateBlock(event.getBlock());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onExplode(BlockExplodeEvent event) {
    if (event.isCancelled()) {
      return;
    }
    for (Block exploded : event.blockList()) {
      termintateBlock(exploded);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFade(BlockFadeEvent event) {
    if (event.isCancelled()) {
      return;
    }
    termintateBlock(event.getBlock());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFertilize(BlockFertilizeEvent event) {
    if (event.isCancelled()) {
      return;
    }
    termintateBlock(event.getBlock());
    for (BlockState state : event.getBlocks()) {
      termintateBlock(state.getBlock());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onForm(BlockFormEvent event) {
    if (event.isCancelled()) {
      return;
    }
    termintateBlock(event.getBlock());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onGrow(BlockGrowEvent event) {
    if (event.isCancelled()) {
      return;
    }
    termintateBlock(event.getBlock());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSpread(BlockSpreadEvent event) {
    if (event.isCancelled()) {
      return;
    }
    termintateBlock(event.getBlock());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEForm(EntityBlockFormEvent event) {
    if (event.isCancelled()) {
      return;
    }
    termintateBlock(event.getBlock());
  }

}