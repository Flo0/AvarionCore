package com.gestankbratwurst.avarioncore.util.common;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.gestankbratwurst.avarioncore.AvarionCore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LaLaLand-CorePlugin and was created at the 22.11.2019
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class BukkitTime implements Listener {

  private static BukkitTime instance;
  @Getter
  private static long lastTickNanoStart = System.nanoTime();
  @Getter
  private static long lastTickMilliStart = lastTickNanoStart / 1000000L;

  public static long getTickMillis() {
    return (System.nanoTime() / 1000000L) - lastTickMilliStart;
  }

  public static long getTickNanos() {
    return System.nanoTime() - lastTickNanoStart;
  }

  public static boolean isMsElapsed(final long millis) {
    return getTickMillis() >= millis;
  }

  public static boolean isNsElapsed(final long nanos) {
    return getTickNanos() >= nanos;
  }

  public static void start(final AvarionCore plugin) {
    instance = new BukkitTime(plugin);
    Bukkit.getPluginManager().registerEvents(instance, plugin);
  }

  private BukkitTime(final AvarionCore plugin) {
  }

  @EventHandler
  public void onTimeStart(final ServerTickStartEvent event) {
    lastTickNanoStart = System.nanoTime();
    lastTickMilliStart = lastTickNanoStart / 1000000L;
  }

}
