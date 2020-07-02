package com.gestankbratwurst.avarioncore.util.packets.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.gestankbratwurst.avarioncore.util.events.PlayerReceiveChunkEvent;
import com.gestankbratwurst.avarioncore.util.events.PlayerUnloadsChunkEvent;
import java.util.List;
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
public class ChunkTracker {

  public static void init(JavaPlugin host, ProtocolManager protocolManager) {

    protocolManager.addPacketListener(new PacketAdapter(host, PacketType.Play.Server.MAP_CHUNK) {

      @Override
      public void onPacketSending(PacketEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.MAP_CHUNK) {
          if (event.getPacket().getMeta("phoenix-ignore").isPresent()) return;

          Bukkit.getScheduler().runTask(host, () ->{
            PacketContainer packet = event.getPacket();
            List<Integer> coords = packet.getIntegers().getValues();
            Player player = event.getPlayer();
            long chunkKey = (long) coords.get(0) & 0xffffffffL | ((long) coords.get(1) & 0xffffffffL) << 32;

            PlayerReceiveChunkEvent e = new PlayerReceiveChunkEvent(player, chunkKey);
            Bukkit.getPluginManager().callEvent(e);
          });

        }
      }
    });


    protocolManager.addPacketListener(new PacketAdapter(host, PacketType.Play.Server.UNLOAD_CHUNK) {

      @Override
      public void onPacketSending(PacketEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
          if (event.getPacket().getMeta("phoenix-ignore").isPresent()) return;

          Bukkit.getScheduler().runTask(host, () ->{
            PacketContainer packet = event.getPacket();
            List<Integer> coords = packet.getIntegers().getValues();
            Player player = event.getPlayer();
            long chunkKey = (long) coords.get(0) & 0xffffffffL | ((long) coords.get(1) & 0xffffffffL) << 32;

            PlayerUnloadsChunkEvent e = new PlayerUnloadsChunkEvent(player, chunkKey);
            Bukkit.getPluginManager().callEvent(e);
          });
        }
      }
    });
  }
}
