package com.gestankbratwurst.avarioncore.util.common;

import com.gestankbratwurst.avarioncore.AvarionCore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import org.bukkit.NamespacedKey;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of CorePlugin and was created at the 31.12.2019
 *
 * CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class NameSpaceFactory {

  public static void init(AvarionCore plugin) {
    if (instance == null) {
      instance = new NameSpaceFactory(plugin);
    }
  }

  private static NameSpaceFactory instance;

  private NameSpaceFactory(AvarionCore plugin) {
    cachedKeys = new Object2ObjectOpenHashMap<>();
    this.plugin = plugin;
  }

  private final AvarionCore plugin;
  private final Map<String, NamespacedKey> cachedKeys;

  public static NamespacedKey provide(String key) {
    NamespacedKey nsk = instance.cachedKeys.get(key);
    if (nsk == null) {
      nsk = new NamespacedKey(instance.plugin, key);
      instance.cachedKeys.put(key, nsk);
    }
    return nsk;
  }

}
