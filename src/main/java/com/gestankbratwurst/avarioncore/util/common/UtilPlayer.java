package com.gestankbratwurst.avarioncore.util.common;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.tasks.TeleportParticleThread;
import com.gestankbratwurst.avarioncore.util.common.sub.WaitingPlayer;
import com.gestankbratwurst.avarioncore.util.events.PlayerReceiveEntityEvent;
import com.gestankbratwurst.avarioncore.util.events.PlayerUnloadsEntityEvent;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LaLaLand-CorePlugin and was created at the 17.11.2019
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class UtilPlayer implements Listener, Runnable {

  private static UtilPlayer instance;
  private static final boolean initialized = false;
  private static final Vector UP_VEC = new Vector(0, 1, 0);
  private static final Vector DOWN_VEC = new Vector(0, -1, 0);
  private static final Map<String, String[]> TEXTURE_CASHE = new Object2ObjectOpenHashMap<>();
  private static final Map<String, UUID> NAME_CASHE = new Object2ObjectOpenHashMap<>();
  private static final Object2ObjectMap<Player, WaitingPlayer> WAITING_PLAYERS = new Object2ObjectOpenHashMap<>();
  private final Object2ObjectOpenHashMap<Player, IntSet> playerViews;
  private final Object2FloatOpenHashMap<Player> attackCooldowns;
  private static final AsyncLoadingCache<UUID, String> PLAYER_NAME_CACHE = Caffeine.newBuilder()
      .expireAfterAccess(60, TimeUnit.MINUTES)
      .buildAsync(requestID -> Bukkit.getOfflinePlayer(requestID).getName());

  private UtilPlayer() {
    playerViews = new Object2ObjectOpenHashMap<>();
    Bukkit.getOnlinePlayers()
        .forEach(player -> playerViews.put(player, new IntOpenHashSet())); //  fix for reloads
    attackCooldowns = new Object2FloatOpenHashMap<>();
  }

  public static void init(final AvarionCore host) {
    Preconditions.checkArgument(!initialized, "UtilPlayer is already initialized!");
    instance = new UtilPlayer();
    Bukkit.getPluginManager().registerEvents(instance, host);
    host.getTaskManager().runRepeatedBukkit(instance, 1L, 1L);
  }

  public static CompletableFuture<String> getPlayerName(final UUID playerID) {
    return PLAYER_NAME_CACHE.get(playerID);
  }

  @Nullable
  public static Block getBlockLookingAt(Player player, double maxDistance, FluidCollisionMode collisionMode) {
    Location eyeLoc = player.getEyeLocation();
    Vector direction = eyeLoc.getDirection();
    RayTraceResult traceResult = player.getWorld().rayTraceBlocks(eyeLoc, direction, maxDistance, collisionMode);
    if (traceResult == null) {
      return null;
    }
    return traceResult.getHitBlock();
  }

  public static Block getBlockLookingAt(Player player, double maxDistance) {
    return getBlockLookingAt(player, maxDistance, FluidCollisionMode.NEVER);
  }

  public static void forceWait(final Player player, final int ticks, final boolean cancelOnDamage, final Consumer<Player> afterwait,
      final Consumer<Player> onCancel) {
    WAITING_PLAYERS.put(player, new WaitingPlayer(player, ticks, cancelOnDamage, afterwait, onCancel));
  }

  public static ItemStack getHead(OfflinePlayer player) {
    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta headMeta = (SkullMeta) head.getItemMeta();
    headMeta.setOwningPlayer(player);
    head.setItemMeta(headMeta);
    return head;
  }

  public static void teleport(Player player, int cdTicks, double particleRadius, Location destination) {
    TeleportParticleThread warmup = new TeleportParticleThread(player, cdTicks, 1.5);
    double secs = UtilMath.cut((double) cdTicks / 20D, 1);
    player.sendMessage("§6Teleportiere in §e" + secs + "§6 Sekunden.");
    UtilPlayer.forceWait(player, cdTicks, true, p -> {
      player.sendMessage("§6Nicht bewegen.");
      player.teleport(destination);
      playSound(player, Sound.BLOCK_PORTAL_TRAVEL, 0.8F, 1.25F);
      warmup.cancel();
    }, cancel -> {
      player.sendMessage("§6Teleport abgebrochen...");
      warmup.cancel();
    });
  }

  public static void teleport(Player player, int cdTicks, Location destination) {
    teleport(player, cdTicks, 1.5, destination);
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    playerViews.put(event.getPlayer(), new IntOpenHashSet());
  }

  @EventHandler
  public void onDamage(final EntityDamageByEntityEvent event) {
    final Entity entity = event.getEntity();
    if (!(entity instanceof Player)) {
      return;
    }
    final WaitingPlayer waiting = WAITING_PLAYERS.get((Player) entity);
    if (waiting == null) {
      return;
    }
    if (waiting.isCancelOnDamage()) {
      waiting.cancel();
      WAITING_PLAYERS.remove((Player) entity);
    }
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    attackCooldowns.remove(event.getPlayer());
    playerViews.remove(event.getPlayer());
    WAITING_PLAYERS.remove(event.getPlayer());
  }

  @EventHandler
  public void onMoving(final PlayerMoveEvent event) {
    if (!WAITING_PLAYERS.containsKey(event.getPlayer())) {
      return;
    }
    final Location from = event.getFrom();
    final Location to = event.getTo();
    if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
      WAITING_PLAYERS.get(event.getPlayer()).cancel();
      WAITING_PLAYERS.remove(event.getPlayer());
    }
  }

  @EventHandler
  public void onEntityShowing(final PlayerReceiveEntityEvent event) {
    playerViews.get(event.getPlayer()).add(event.getEntityID());
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityHiding(final PlayerUnloadsEntityEvent event) {
    final IntSet ints = playerViews.get(event.getPlayer());
    for (final int id : event.getEntityIDs()) {
      ints.remove(id);
    }
  }

  public static float getAttackCooldown(final Player player) {
    return instance.attackCooldowns.getFloat(player);
  }

  public static IntSet getEntityViewOf(final Player player) {
    return instance.playerViews.get(player);
  }

  public static BlockFace getExactFacing(final Player player) {
    final Vector direction = player.getEyeLocation().getDirection();
    if (direction.angle(UP_VEC) <= (Math.PI / 4)) {
      return BlockFace.UP;
    } else if (direction.angle(DOWN_VEC) <= (Math.PI / 4)) {
      return BlockFace.DOWN;
    }
    return player.getFacing();
  }

  public static void playSound(final Player player, final Sound sound, final float volume, final float pitch) {
    player.playSound(player.getEyeLocation(), sound, pitch, volume);
  }


  public static void playSound(final Player player, final Sound sound) {
    playSound(player, sound, 1F, 1F);
  }

  public static String getEncodedTexture(final String url) {
    final byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
    return new String(encodedData);
  }

  public static String[] getSkinFromID(final UUID id) {
    final String uuid = id.toString().replaceAll("-", "");
    if (TEXTURE_CASHE.containsKey(uuid)) {
      return TEXTURE_CASHE.get(uuid);
    }
    try {
      final URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
      final InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
      final JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0)
          .getAsJsonObject();
      final String texture = textureProperty.get("value").getAsString();
      final String signature = textureProperty.get("signature").getAsString();

      final String[] infos = new String[]{texture, signature};

      TEXTURE_CASHE.put(uuid, infos);

      return infos;
    } catch (final IOException e) {
      System.err.println("Could not get skin data from session servers!");
      e.printStackTrace();
      return null;
    }
  }

  public static String[] getSkinFromName(final String name) {
    final UUID uuid;
    if (NAME_CASHE.containsKey(name)) {
      uuid = NAME_CASHE.get(name);
    } else {
      try {
        final URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        final InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
        uuid = UtilText.uuidFromShortString(new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString());
        NAME_CASHE.put(name, uuid);
      } catch (final IOException e) {
        System.err.println("Could not get skin data from session servers!");
        e.printStackTrace();
        return null;
      }
    }
    return getSkinFromID(uuid);
  }

  @Override
  public void run() {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      attackCooldowns.put(player, player.getCooledAttackStrength(0F));
    }
    if (!WAITING_PLAYERS.isEmpty()) {
      final Set<WaitingPlayer> removers = Sets.newHashSet();
      for (final WaitingPlayer waiting : WAITING_PLAYERS.values()) {
        if (waiting.lookup()) {
          removers.add(waiting);
        }
      }
      for (final WaitingPlayer waiting : removers) {
        WAITING_PLAYERS.remove(waiting.getPlayer());
      }
    }
  }
}
