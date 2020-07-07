package com.gestankbratwurst.avarioncore.economy.adminshops;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.gestankbratwurst.avarioncore.AvarionCore;
import java.util.List;
import net.crytec.libs.protocol.util.WrapperPlayClientSetCreativeSlot;
import net.crytec.libs.protocol.util.WrapperPlayServerSetSlot;
import net.crytec.libs.protocol.util.WrapperPlayServerWindowItems;
import org.bukkit.inventory.ItemStack;

public class ShopInventoryPacketAdapter extends PacketAdapter {

  public ShopInventoryPacketAdapter() {
    super(AvarionCore.getInstance(), Server.SET_SLOT, Server.WINDOW_ITEMS, Client.SET_CREATIVE_SLOT);
  }

  @Override
  public void onPacketReceiving(final PacketEvent event) {
    final WrapperPlayClientSetCreativeSlot wrapper = new WrapperPlayClientSetCreativeSlot(event.getPacket());
    final ItemStack item = wrapper.getClickedItem();

    // Check if item is shop item?

    // Handle Item

    wrapper.setClickedItem(item);
    event.setPacket(wrapper.getHandle());
  }

  @Override
  public void onPacketSending(final PacketEvent event) {

    if (!event.getPlayer().getScoreboardTags().contains("IN_SHOP")) {
      System.out.println("Not in shop.");
      return;
    }
    System.out.println("Is in shop.");

    final PacketContainer packet = event.getPacket();

    if (event.getPacketType() == Server.SET_SLOT) {
      final WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(packet);
      final ItemStack item = wrapper.getSlotData();

      // Check if item is shop item?
      System.out.println("SET_SLOT");

      // Handle Item

      wrapper.setSlotData(item);
      event.setPacket(wrapper.getHandle());
    } else if (event.getPacketType() == Server.WINDOW_ITEMS) {
      final WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(packet);
      wrapper.getWindowId();
      final List<ItemStack> items = wrapper.getSlotData();

      // Check if item is shop item?
      System.out.println("WINDOW_ITEMS: x" + items.size());

      // Handle Item List

      wrapper.setSlotData(items);
      event.setPacket(wrapper.getHandle());
    }
  }
}
