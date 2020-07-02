package com.gestankbratwurst.avarioncore.protection;

import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.LongConsumer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 01.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class WorldDomain {

  public WorldDomain(JsonObject jsonObject) {
    this.chunks = new Long2ObjectOpenHashMap<>();
    this.regionSet = new ObjectOpenHashSet<>();
    this.worldID = UUID.fromString(jsonObject.get("WorldID").getAsString());
    JsonArray regionArray = jsonObject.get("Regions").getAsJsonArray();
    for (JsonElement element : regionArray) {
      this.addRegion(new ProtectedRegion(element.getAsJsonObject()));
    }
    this.playerRules = new RuleSet(jsonObject.get("PlayerRules").getAsJsonObject());
    this.globalRules = new RuleSet(jsonObject.get("GlobalRules").getAsJsonObject());
  }

  public WorldDomain(UUID worldID) {
    this.chunks = new Long2ObjectOpenHashMap<>();
    this.regionSet = new ObjectOpenHashSet<>();
    this.worldID = worldID;
    this.playerRules = new RuleSet(false);
    this.globalRules = new RuleSet(true);
  }

  @Getter
  @Setter
  private int worldPlayerRulePriority = 0;
  @Getter
  @Setter
  private int worldGlobalRulePriority = 0;
  private final UUID worldID;
  private final Long2ObjectMap<ChunkDomain> chunks;
  private final Set<ProtectedRegion> regionSet;
  private final RuleSet playerRules;
  private final RuleSet globalRules;

  @Nullable
  public ChunkDomain getChunkDomain(long chunkKey) {
    return chunks.get(chunkKey);
  }

  @Nullable
  public ProtectedRegion getHighestPriorityRegionAt(final Block block) {
    ChunkDomain chunkDomain = getChunkDomain(block.getChunk().getChunkKey());
    return chunkDomain == null ? null : chunkDomain.getHighestPriorityRegion(block.getLocation().toVector());
  }

  public void setWorldPlayerRule(ProtectionRule rule, RuleState state) {
    playerRules.setState(rule, state);
  }

  public void setWorldGlobalRule(ProtectionRule rule, RuleState state) {
    globalRules.setState(rule, state);
  }

  public RuleSet getPlayerRules(Block block, AvarionPlayer avarionPlayer) {
    ProtectedRegion region = getHighestPriorityRegionAt(block);
    if (region == null || region.getPriority() < this.worldPlayerRulePriority || avarionPlayer.getPlayerID().equals(region.getOwnerID())) {
      return this.playerRules;
    } else if (avarionPlayer.getFriendAccount().isFriend(region.getOwnerID())) {
      return region.getFriendRuleSet();
    } else {
      return region.getVisitorRuleSet();
    }
  }

  public RuleSet getEnvironmentRules(Block block) {
    ProtectedRegion region = getHighestPriorityRegionAt(block);
    if (region == null || region.getPriority() < worldGlobalRulePriority) {
      return this.globalRules;
    }
    return region.getGlobalRuleSet();
  }

  public void addRegion(final ProtectedRegion protectedRegion) {
    regionSet.add(protectedRegion);
    protectedRegion.forEach((LongConsumer) chunkKey -> {
      ChunkDomain chunkDomain = chunks.get(chunkKey);
      if (chunkDomain == null) {
        chunkDomain = new ChunkDomain();
      }
      chunkDomain.addRegion(protectedRegion);
      chunks.put(chunkKey, chunkDomain);
    });
  }

  @NotNull
  public JsonObject getAsJson() {
    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("WorldID", worldID.toString());

    JsonArray regionArray = new JsonArray();

    for (ProtectedRegion region : regionSet) {
      regionArray.add(region.getAsJson());
    }

    jsonObject.add("PlayerRules", playerRules.getAsJson());
    jsonObject.add("GlobalRules", globalRules.getAsJson());
    jsonObject.add("Regions", regionArray);
    return jsonObject;
  }

}
