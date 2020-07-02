package com.gestankbratwurst.avarioncore.util.events;

import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 25.04.2020
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class PlayerChangeChunkEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();

  public PlayerChangeChunkEvent(Player who, Chunk fromChunk, Chunk toChunk) {
    super(who);
    this.fromChunk = fromChunk;
    this.toChunk = toChunk;
  }

  @Getter
  private final Chunk fromChunk;
  @Getter
  private final Chunk toChunk;

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }

}
