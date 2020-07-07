package com.gestankbratwurst.avarioncore.economy.direct;

import com.gestankbratwurst.avarioncore.AvarionCore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 07.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class DirectTradeManager implements Listener {

  public DirectTradeManager(final AvarionCore plugin) {
    this.sentRequests = new Object2ObjectOpenHashMap<>();
    this.receivedRequests = new Object2ObjectOpenHashMap<>();
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  private final Map<UUID, UUID> sentRequests;
  private final Map<UUID, Set<UUID>> receivedRequests;

  public void sendRequest(final Player from, final Player to) {
    
  }

  public void hasRequest(final Player from, final Player to) {

  }

  public void declineRequest(final Player from, final Player to) {

  }

  public void initiateTrade(final Player sender, final Player receiver) {

  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    final UUID playerID = event.getPlayer().getUniqueId();
    final Set<UUID> received = this.receivedRequests.remove(playerID);
    for (final UUID sender : received) {
      this.sentRequests.remove(sender);
    }
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    this.receivedRequests.put(event.getPlayer().getUniqueId(), new HashSet<>());
  }

}
