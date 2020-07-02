package com.gestankbratwurst.avarioncore.util.events;

import org.bukkit.World;
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
public class PlayerReceiveEntityEvent extends PlayerEvent {

  private final int entityID;

  public PlayerReceiveEntityEvent(Player who, int entityID) {
    super(who);
    this.entityID = entityID;
  }

  public int getEntityID() {
    return entityID;
  }

  private static final HandlerList handlers = new HandlerList();

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

}
