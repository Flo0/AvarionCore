package com.gestankbratwurst.avarioncore.util.common.sub;

import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of CorePlugin and was created at the 13.12.2019
 *
 * CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class WaitingPlayer {

  public WaitingPlayer(final Player player, final int ticks, final boolean cancelOnDamage, final Consumer<Player> afterWait,
      final Consumer<Player> onCancel) {
    this.player = player;
    this.afterWait = afterWait;
    this.onCancel = onCancel;
    ticksLeft = ticks;
    this.cancelOnDamage = cancelOnDamage;
  }

  @Getter
  private final Player player;
  private final Consumer<Player> afterWait;
  private final Consumer<Player> onCancel;
  private int ticksLeft;
  @Getter
  private final boolean cancelOnDamage;

  public void cancel() {
    if (player.isOnline()) {
      onCancel.accept(player);
    }
  }

  public boolean lookup() {
    ticksLeft--;
    if (ticksLeft == 0) {
      if (player.isOnline()) {
        afterWait.accept(player);
      }
      return true;
    }
    return false;
  }

}
