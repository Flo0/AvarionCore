package com.gestankbratwurst.avarioncore.tasks;

import com.google.common.collect.Queues;
import java.util.ArrayDeque;
import java.util.function.Consumer;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of CorePlugin and was created at the 03.01.2020
 *
 * CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class BukkitSyncConsumer<T> implements Runnable {

  private final ArrayDeque<Pair<ItemStack, Consumer<ItemStack>>> itemActions = Queues.newArrayDeque();

  public void addAction(ItemStack item, Consumer<ItemStack> action) {
    itemActions.add(Pair.of(item, action));
  }

  public void invokeOne() {
    if (!itemActions.isEmpty()) {
      Pair<ItemStack, Consumer<ItemStack>> pair = itemActions.poll();
      pair.getRight().accept(pair.getLeft());
    }
  }


  public void addEnachntAction(ItemStack item) {
    addAction(item, itm -> {
      item.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
    });
  }

  public BukkitSyncConsumer(T object, Consumer<T> action, JavaPlugin plugin) {
    this.plugin = plugin;
    sync = new ConsumingBukkitSync<>(object, action);
  }

  private final JavaPlugin plugin;
  private final ConsumingBukkitSync<T> sync;

  @Override

  public void run() {
    sync.runTask(plugin);
  }

}
