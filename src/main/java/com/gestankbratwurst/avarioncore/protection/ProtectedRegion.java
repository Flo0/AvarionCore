package com.gestankbratwurst.avarioncore.protection;

import com.gestankbratwurst.avarioncore.util.common.UtilVect;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.UUID;
import java.util.function.LongConsumer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 30.06.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ProtectedRegion implements Comparable<ProtectedRegion>, LongIterable {

  public ProtectedRegion(JsonObject jsonObject) {
    this.priority = jsonObject.get("Priority").getAsInt();
    this.ownerID = UUID.fromString(jsonObject.get("OwnerID").getAsString());
    this.regionBox = UtilVect.stringToBox(jsonObject.get("Region").getAsString());
    this.regionName = jsonObject.get("Name").getAsString();
    this.visitorRuleSet = new RuleSet(jsonObject.get("VisitorRules").getAsJsonObject());
    this.friendRuleSet = new RuleSet(jsonObject.get("FriendRules").getAsJsonObject());
    this.globalRuleSet = new RuleSet(jsonObject.get("GlobalRules").getAsJsonObject());
  }

  public ProtectedRegion(BoundingBox regionBox, UUID ownerID, int priority) {
    this.regionBox = regionBox;
    this.ownerID = ownerID;
    this.priority = priority;
    this.visitorRuleSet = new RuleSet(false);
    this.friendRuleSet = new RuleSet(false);
    this.globalRuleSet = new RuleSet(true);
  }

  @Getter
  private final int priority;
  @Getter
  private final BoundingBox regionBox;
  @Getter
  private final UUID ownerID;
  @Getter
  @Setter
  private String regionName = "UNBENANNT";
  @Getter
  private final RuleSet visitorRuleSet;
  @Getter
  private final RuleSet friendRuleSet;
  @Getter
  private final RuleSet globalRuleSet;

  public LongList getChunkKeysInRegion() {
    LongList list = new LongArrayList();
    this.forEach((LongConsumer) list::add);
    return list;
  }

  @Override
  public int compareTo(@NotNull ProtectedRegion other) {
    return this.priority - other.priority;
  }

  public JsonObject getAsJson() {
    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("Priority", priority);
    jsonObject.addProperty("OwnerID", ownerID.toString());
    jsonObject.addProperty("Region", UtilVect.boxToString(regionBox));
    jsonObject.addProperty("Name", regionName);
    jsonObject.add("VisitorRules", visitorRuleSet.getAsJson());
    jsonObject.add("FriendRules", friendRuleSet.getAsJson());
    jsonObject.add("GlobalRules", globalRuleSet.getAsJson());

    return jsonObject;
  }

  @Override
  @NotNull
  public LongIterator iterator() {
    return getChunkKeysInRegion().iterator();
  }

  @Override
  public void forEach(LongConsumer action) {
    Vector max = regionBox.getMax();
    Vector min = regionBox.getMin();

    int maxX = (int) Math.floor(max.getX()) >> 4;
    int maxZ = (int) Math.floor(max.getZ()) >> 4;
    int minX = (int) Math.floor(min.getX()) >> 4;
    int minZ = (int) Math.floor(min.getZ()) >> 4;

    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {
        action.accept(Chunk.getChunkKey(x, z));
      }
    }
  }

}