package com.gestankbratwurst.avarioncore.util.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LaLaLand-CorePlugin and was created at the 17.11.2019
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class PlayerUnloadsEntityEvent extends PlayerEvent {

  private final int[] entityIDs;

  public PlayerUnloadsEntityEvent(Player who, int[] entityIDs) {
    super(who);
    this.entityIDs = entityIDs;
  }

  public int[] getEntityIDs() {
    return entityIDs;
  }

  private static final HandlerList handlers = new HandlerList();

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

}
