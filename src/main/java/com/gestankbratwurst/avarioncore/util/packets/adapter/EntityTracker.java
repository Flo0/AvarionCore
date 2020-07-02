package com.gestankbratwurst.avarioncore.util.packets.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.gestankbratwurst.avarioncore.util.events.PlayerReceiveEntityEvent;
import com.gestankbratwurst.avarioncore.util.events.PlayerUnloadsEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LaLaLand-CorePlugin and was created at the 17.11.2019
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class EntityTracker {

  public static void init(JavaPlugin host, ProtocolManager protocolManager) {
    protocolManager.addPacketListener(new PacketAdapter(host, PacketType.Play.Server.SPAWN_ENTITY_LIVING) {

      @Override
      public void onPacketSending(PacketEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
          if (event.getPacket().getMeta("phoenix-ignore").isPresent()) return;

          PacketContainer packet = event.getPacket();

          Player viewer = event.getPlayer();
          int entityID = packet.getIntegers().read(0);

          PlayerReceiveEntityEvent recieveEvent = new PlayerReceiveEntityEvent(viewer, entityID);
          Bukkit.getScheduler().runTask(host, () -> Bukkit.getPluginManager().callEvent(recieveEvent));
        }
      }
    });


    protocolManager.addPacketListener(new PacketAdapter(host, PacketType.Play.Server.ENTITY_DESTROY) {

      @Override
      public void onPacketSending(PacketEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.ENTITY_DESTROY) {
          if (event.getPacket().getMeta("phoenix-ignore").isPresent()) return;

          PacketContainer packet = event.getPacket();

          int[] entityIDs = packet.getIntegerArrays().getValues().get(0);
          Player viewer = event.getPlayer();

          PlayerUnloadsEntityEvent unloadEvent = new PlayerUnloadsEntityEvent(viewer, entityIDs);
          Bukkit.getScheduler().runTask(host, () -> Bukkit.getPluginManager().callEvent(unloadEvent));
        }
      }
    });
  }
}
