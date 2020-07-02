package com.gestankbratwurst.avarioncore.resourcepack.sounds;

import net.minecraft.server.v1_16_R1.MinecraftKey;
import net.minecraft.server.v1_16_R1.PacketPlayOutCustomSoundEffect;
import net.minecraft.server.v1_16_R1.SoundCategory;
import net.minecraft.server.v1_16_R1.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 26.04.2020
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public enum CustomSound {

  COINS_SOUND(),
  RECIPE_INFO();

  private MinecraftKey key = null;

  public void play(Player player) {
    play(player, player.getEyeLocation(), SoundCategory.NEUTRAL, 1F, 1F);
  }

  public void play(Player player, Location location) {
    play(player, location, SoundCategory.NEUTRAL, 1F, 1F);
  }

  public void play(Player player, Location location, float volume, float pitch) {
    play(player, location, SoundCategory.NEUTRAL, volume, pitch);
  }

  public void play(Player player, Location location, SoundCategory soundCategory) {
    play(player, location, soundCategory, 1F, 1F);
  }

  public void play(Player player, SoundCategory soundCategory, float volume, float pitch) {
    play(player, player.getEyeLocation(), soundCategory, volume, pitch);
  }

  public void play(Player player, float volume, float pitch) {
    play(player, player.getEyeLocation(), SoundCategory.NEUTRAL, volume, pitch);
  }

  public void play(Player player, Location location, SoundCategory soundCategory, float volume, float pitch) {
    if (key == null) {
      key = new MinecraftKey("custom." + this.toString().toLowerCase());
    }
    Vec3D vec = new Vec3D(location.getX(), location.getY(), location.getZ());
    PacketPlayOutCustomSoundEffect packet = new PacketPlayOutCustomSoundEffect(key, soundCategory, vec, volume, pitch);
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
  }

}
