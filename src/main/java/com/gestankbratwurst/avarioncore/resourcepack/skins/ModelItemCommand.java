package com.gestankbratwurst.avarioncore.resourcepack.skins;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Values;
import com.gestankbratwurst.avarioncore.resourcepack.sounds.CustomSound;
import com.gestankbratwurst.avarioncore.util.Msg;
import net.minecraft.server.v1_16_R1.SoundCategory;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 24.11.2019
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@CommandAlias("model")
@CommandPermission("admin")
public class ModelItemCommand extends BaseCommand {

  @Default
  public void onCommand(final Player sender) {
    Msg.send(sender, "Resourcepack", "Benutze '/modelitem get <ModelItem>' um ein ModelItem zu erhalten.");
  }

  @Subcommand("customsound")
  @CommandCompletion("@CustomSound")
  public void onCustomSound(final Player sender, @Values("@CustomSound") CustomSound customSound,
      @Values("@SoundCategory") @Default("NEUTRAL") SoundCategory soundCategory, @Default("1.0") float volume,
      @Default("1.0") float pitch) {
    customSound.play(sender, sender.getLocation(), soundCategory, volume, pitch);
  }

  @Subcommand("asitem")
  @CommandCompletion("@ModelItem")
  public void onGetCommand(final Player sender, final Model model) {
    sender.getInventory().addItem(model.getItem());
    final String modelName = Msg.elem(model.toString());
    Msg.send(sender, "Resourcepack", "Du hast ein ModelItem erhalten: " + modelName);
  }

  @Subcommand("ashead")
  @CommandCompletion("@ModelItem")
  public void onGetSkillCommand(final Player sender, final Model model) {
    sender.getInventory().addItem(model.getHead());
    final String modelName = Msg.elem(model.toString());
    Msg.send(sender, "Resourcepack", "Du hast einen ModelItem Kopf erhalten: " + modelName);
  }

  @Subcommand("tell")
  @CommandCompletion("@ModelItem")
  public void onTellCommand(final Player sender, final Model model) {
    Msg.send(sender, "Resourcepack", "Model: " + model.getChar());
  }

}
