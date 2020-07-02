package com.gestankbratwurst.avarioncore.util.actionbar;

import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.PriorityQueue;
import java.util.function.Supplier;
import lombok.Getter;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 23.03.2020
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ActionBarSection {

  protected ActionBarSection(final ActionBarManager actionBarManager) {
    sectionLines = Queues.newPriorityQueue();
    sectionLines.add(ActionLine.empty());
    this.actionBarManager = actionBarManager;
    tokenLines = new Object2ObjectOpenHashMap<>();
  }

  @Getter
  private final PriorityQueue<ActionLine> sectionLines;
  private final ActionBarManager actionBarManager;
  private final Object2ObjectMap<String, ActionLine> tokenLines;

  public ActionLine addLayer(final int priority, final Supplier<String> lineSupplier) {
    final ActionLine line = new ActionLine(priority, lineSupplier);
    addLayer(line);
    return line;
  }

  public void removeLayer(final ActionLine line) {
    sectionLines.remove(line);
  }

  private void removeToken(final String key, final ActionLine line) {
    if (tokenLines.get(key) == line) {
      removeLayer(line);
      tokenLines.remove(key);
    }
  }

  public void addTempLayer(final long lifeTicks, final ActionLine line) {
    sectionLines.add(line);
    actionBarManager.getTaskManager().runBukkitSyncDelayed(() -> {
      removeLayer(line);
    }, lifeTicks);
  }

  public void addTokenLayer(final long lifeTicks, final String key, final ActionLine line) {
    if (tokenLines.containsKey(key)) {
      sectionLines.remove(tokenLines.get(key));
      tokenLines.put(key, line);
    }
    sectionLines.add(line);
    actionBarManager.getTaskManager().runBukkitSyncDelayed(() -> {
      removeToken(key, line);
    }, lifeTicks);
  }

  public ActionLine addTokenLayer(final long lifeTicks, final String key, final int priority, final Supplier<String> lineSupplier) {
    final ActionLine line = new ActionLine(priority, lineSupplier);
    addTempLayer(lifeTicks, line);
    return line;
  }

  public ActionLine addTempLayer(final long lifeTicks, final int priority, final Supplier<String> lineSupplier) {
    final ActionLine line = new ActionLine(priority, lineSupplier);
    addTempLayer(lifeTicks, line);
    return line;
  }

  public void addLayer(final ActionLine line) {
    sectionLines.add(line);
  }

  public int getHighestPriority() {
    return sectionLines.peek().getPriority();
  }

  public ActionLine getMostSignificant() {
    return sectionLines.peek();
  }

}
