package com.gestankbratwurst.avarioncore.commands.impl.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.gestankbratwurst.avarioncore.util.Msg;
import org.bukkit.entity.Player;

@CommandAlias("speed")
@CommandPermission("admin")
public class SpeedCommand extends BaseCommand {

  @Default
  @CommandCompletion("@range:10 @players")
  public void setSpeed(final Player issuer, int speed, @Optional final OnlinePlayer target) {
    if (speed > 10) {
      speed = 10;
    }
    final float targetSpeed = (float) speed / 10;

    if (target == null) {
      if (issuer.isFlying()) {
        issuer.setFlySpeed(targetSpeed);
      } else {
        issuer.setWalkSpeed(targetSpeed);
      }
      Msg.send(issuer, "Command", "Deine Geschwindigkeit wurde auf " + speed + " gesetzt");
    } else {
      if (target.getPlayer().isFlying()) {
        target.getPlayer().setFlySpeed(targetSpeed);
      } else {
        target.getPlayer().setWalkSpeed(targetSpeed);
      }
      Msg.send(issuer, "Command", "Deine Geschwindigkeit wurde von " + Msg.elem(issuer.getDisplayName()) + " auf " + speed + " gesetzt");
    }


  }

}
