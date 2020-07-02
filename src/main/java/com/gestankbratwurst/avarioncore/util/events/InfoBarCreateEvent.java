package com.gestankbratwurst.avarioncore.util.events;

import com.google.common.collect.Lists;
import com.gestankbratwurst.avarioncore.util.holograms.infobars.InfoLineSpacing;
import com.gestankbratwurst.avarioncore.util.tuples.Pair;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LaLaLand-CorePlugin and was created at the 17.11.2019
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class InfoBarCreateEvent extends Event implements Cancellable {

  private static final HandlerList handlers = new HandlerList();

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public InfoBarCreateEvent(Entity entity) {
    this.entity = entity;
    this.lines = Lists.newArrayList();
  }

  private final Entity entity;
  private final List<Pair<String, InfoLineSpacing>> lines;

  private boolean cancelled = false;

  public HandlerList getHandlers() {
    return handlers;
  }

  public Entity getEntity() {
    return entity;
  }

  public List<Pair<String, InfoLineSpacing>> getLines() {
    return lines;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }


}