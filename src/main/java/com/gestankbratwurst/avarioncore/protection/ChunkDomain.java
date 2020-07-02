package com.gestankbratwurst.avarioncore.protection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.bukkit.util.Vector;
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
public class ChunkDomain {

  public ChunkDomain() {
    this.regions = new TreeSet<>();
  }

  private final SortedSet<ProtectedRegion> regions;

  public void addRegion(ProtectedRegion region) {
    this.regions.add(region);
  }

  @Nullable
  public ProtectedRegion getHighestPriorityRegion(Vector vector) {
    for (ProtectedRegion region : regions) {
      if (region.getRegionBox().contains(vector)) {
        return region;
      }
    }
    return null;
  }

  @NotNull
  public List<ProtectedRegion> listRegions() {
    ArrayList<ProtectedRegion> regionList = new ArrayList<>(regions);
    Collections.sort(regionList);
    return regionList;
  }

}