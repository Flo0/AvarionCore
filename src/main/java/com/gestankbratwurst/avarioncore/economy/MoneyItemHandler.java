package com.gestankbratwurst.avarioncore.economy;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionDataManager;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.gestankbratwurst.avarioncore.resourcepack.sounds.CustomSound;
import com.gestankbratwurst.avarioncore.util.common.NameSpaceFactory;
import com.gestankbratwurst.avarioncore.util.items.ItemBuilder;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import net.crytec.libs.protocol.holograms.impl.HologramManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 03.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class MoneyItemHandler implements Listener {

  public MoneyItemHandler(AvarionCore plugin) {
    this.dataManager = plugin.getAvarionDataManager();
    this.hologramManager = plugin.getUtilModule().getHologramManager();

    this.dropItems = ImmutableRangeMap.<Double, ItemStack>builder()
        .put(Range.closedOpen(0D, 1D), Model.COPPER_PILE_TINY.getItem())
        .put(Range.closedOpen(1D, 2D), Model.COPPER_PILE_SMALL.getItem())
        .put(Range.closedOpen(2D, 5D), Model.COPPER_PILE_MEDIUM.getItem())
        .put(Range.closedOpen(5D, 15D), Model.COPPER_PILE_BIG.getItem())
        .put(Range.closedOpen(15D, 30D), Model.COPPER_PILE_HUGE.getItem())
        .put(Range.closedOpen(30D, 60D), Model.SILVER_PILE_TINY.getItem())
        .put(Range.closedOpen(60D, 120D), Model.SILVER_PILE_SMALL.getItem())
        .put(Range.closedOpen(120D, 200D), Model.SILVER_PILE_MEDIUM.getItem())
        .put(Range.closedOpen(200D, 500D), Model.SILVER_PILE_BIG.getItem())
        .put(Range.closedOpen(500D, 1000D), Model.SILVER_PILE_HUGE.getItem())
        .put(Range.closedOpen(1000D, 2000D), Model.GOLD_PILE_TINY.getItem())
        .put(Range.closedOpen(2000D, 4000D), Model.GOLD_PILE_SMALL.getItem())
        .put(Range.closedOpen(4000D, 10000D), Model.GOLD_PILE_MEDIUM.getItem())
        .put(Range.closedOpen(10000D, 25000D), Model.GOLD_PILE_BIG.getItem())
        .put(Range.closedOpen(25000D, 100000D), Model.GOLD_PILE_HUGE.getItem())
        .put(Range.closedOpen(100000D, 250000D), Model.GOLD_PILE_BAR_SMALL.getItem())
        .put(Range.closedOpen(250000D, 500000D), Model.GOLD_PILE_BAR_MEDIUM.getItem())
        .put(Range.atLeast(500000D), Model.GOLD_PILE_BAR_BIG.getItem())
        .build();
    this.iconItems = ImmutableRangeMap.<Double, ItemStack>builder()
        .put(Range.closedOpen(0D, 1D), Model.COINS_COPPER_0.getItem())
        .put(Range.closedOpen(1D, 2D), Model.COINS_COPPER_2.getItem())
        .put(Range.closedOpen(2D, 5D), Model.COINS_COPPER_3.getItem())
        .put(Range.closedOpen(5D, 15D), Model.COINS_COPPER_4.getItem())
        .put(Range.closedOpen(15D, 30D), Model.COINS_COPPER_5.getItem())
        .put(Range.closedOpen(30D, 60D), Model.COINS_SILVER_0.getItem())
        .put(Range.closedOpen(60D, 120D), Model.COINS_SILVER_2.getItem())
        .put(Range.closedOpen(120D, 200D), Model.COINS_SILVER_3.getItem())
        .put(Range.closedOpen(200D, 500D), Model.COINS_SILVER_4.getItem())
        .put(Range.closedOpen(500D, 1000D), Model.COINS_SILVER_5.getItem())
        .put(Range.closedOpen(1000D, 2000D), Model.COINS_GOLD_0.getItem())
        .put(Range.closedOpen(2000D, 4000D), Model.COINS_GOLD_2.getItem())
        .put(Range.closedOpen(4000D, 10000D), Model.COINS_GOLD_3.getItem())
        .put(Range.closedOpen(10000D, 25000D), Model.COINS_GOLD_4.getItem())
        .put(Range.closedOpen(25000D, 100000D), Model.COINS_GOLD_5.getItem())
        .put(Range.closedOpen(100000D, 250000D), Model.BARS_GOLD_0.getItem())
        .put(Range.closedOpen(250000D, 500000D), Model.BARS_GOLD_1.getItem())
        .put(Range.atLeast(500000D), Model.BARS_GOLD_2.getItem())
        .build();

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  private final AvarionDataManager dataManager;
  private final HologramManager hologramManager;
  private final RangeMap<Double, ItemStack> iconItems;
  private final RangeMap<Double, ItemStack> dropItems;

  public ItemStack getIcon(double amount) {
    amount = ((int)(amount * 100)) / 100D;
    ItemStack icon = iconItems.get(amount);
    if (icon == null) {
      throw new IllegalArgumentException("Amount out of range.");
    }
    return new ItemBuilder(icon.clone()).name("§f" + amount + " " + EconomyAccount.MONEY_PLURAL).build();
  }

  public Item dropMoneyItem(Location location, double amount) {
    amount = ((int)(amount * 100)) / 100D;
    ItemStack pile = dropItems.get(amount);
    if (pile == null) {
      throw new IllegalArgumentException("Amount out of range.");
    }
    ItemStack drop = new ItemBuilder(pile.clone()).name("§f" + amount + " " + EconomyAccount.MONEY_PLURAL).build();
    Item item = location.getWorld().dropItemNaturally(location, drop);
    item.getPersistentDataContainer().set(NameSpaceFactory.provide("MONEY"), PersistentDataType.DOUBLE, amount);
    return item;
  }

  @EventHandler
  public void onPickup(EntityPickupItemEvent event) {
    Item item = event.getItem();
    PersistentDataContainer container = item.getPersistentDataContainer();
    Double money = container.get(NameSpaceFactory.provide("MONEY"), PersistentDataType.DOUBLE);
    if (money != null) {
      event.setCancelled(true);
      if (!(event.getEntity() instanceof Player)) {
        return;
      }
      Player player = (Player) event.getEntity();
      item.remove();
      AvarionPlayer avarionPlayer = dataManager.getOnlineData(player.getUniqueId());
      if (avarionPlayer == null) {
        throw new IllegalStateException("Offline player picked up item... what?");
      }
      avarionPlayer.getEconomyAccount().add(money);
      CustomSound.COINS_SOUND.play(player, 0.33F, 0.9F);
      hologramManager.createMovingHologram(item.getLocation(), new Vector(0, 0.2, 0), 12)
          .getHologram()
          .appendTextLine("§a+ " + money + EconomyAccount.MONEY_PLURAL);
    }
  }

}
