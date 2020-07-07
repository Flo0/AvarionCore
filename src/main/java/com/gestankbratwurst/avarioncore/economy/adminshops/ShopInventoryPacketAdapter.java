package com.gestankbratwurst.avarioncore.economy.adminshops;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionDataManager;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.economy.EconomyAccount;
import com.gestankbratwurst.avarioncore.economy.ItemCostEvaluator;
import com.gestankbratwurst.avarioncore.util.items.ItemBuilder;
import com.gestankbratwurst.avarioncore.util.nbtapi.NBTItem;
import java.util.List;
import net.crytec.libs.protocol.util.WrapperPlayClientSetCreativeSlot;
import net.crytec.libs.protocol.util.WrapperPlayServerSetSlot;
import net.crytec.libs.protocol.util.WrapperPlayServerWindowItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopInventoryPacketAdapter extends PacketAdapter {

  public ShopInventoryPacketAdapter(final AvarionCore plugin) {
    super(plugin, Server.SET_SLOT, Server.WINDOW_ITEMS);
    this.avarionDataManager = plugin.getAvarionDataManager();
    this.costEvaluator = plugin.getEconomyManager().getItemCostEvaluator();
  }

  private final AvarionDataManager avarionDataManager;
  private final ItemCostEvaluator costEvaluator;

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

    final AvarionPlayer avarionPlayer = this.avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());

    if (avarionPlayer == null) {
      return;
    }

    final AdminShop shop = avarionPlayer.getCurrentlyVisitedShop();

    if (shop == null) {
      return;
    }

    final PacketContainer packet = event.getPacket();

    if (event.getPacketType() == Server.SET_SLOT) {
      final WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(packet);
      final ItemStack item = wrapper.getSlotData();

      if (item == null || item.getType() == Material.AIR) {
        return;
      }

      // Check if item is shop item?
      final NBTItem nbt = new NBTItem(item);
      if (!nbt.hasKey("SHOP_ICON")) {
        final ShopType shopType = shop.getShopType();
        final ItemBuilder packetItemBuilder = new ItemBuilder(item.clone());

        this.convertSlotItem(avarionPlayer, item, shopType, packetItemBuilder);
        wrapper.setSlotData(packetItemBuilder.build());
      }
      event.setPacket(wrapper.getHandle());
    } else if (event.getPacketType() == Server.WINDOW_ITEMS) {

      final WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(packet);

      final List<ItemStack> itemData = wrapper.getSlotData();

      for (int i = 0; i < itemData.size(); i++) {
        final ItemStack slotItem = itemData.get(i);

        if (slotItem == null || slotItem.getType() == Material.AIR) {
          continue;
        }

        final NBTItem nbt = new NBTItem(slotItem);
        if (!nbt.hasKey("SHOP_ICON")) {
          final ShopType shopType = shop.getShopType();
          final ItemBuilder packetItemBuilder = new ItemBuilder(slotItem);
          this.convertSlotItem(avarionPlayer, slotItem, shopType, packetItemBuilder);
          packetItemBuilder.build();
        }
      }

      System.out.println("Items sent: " + wrapper.getSlotData().size());
    }
  }

  private void convertSlotItem(final AvarionPlayer avarionPlayer, final ItemStack slotItem, final ShopType shopType,
      final ItemBuilder packetItemBuilder) {
    if (shopType.isTradableHere(slotItem)) {
      final double price = this.costEvaluator.getPlayerSellCost(slotItem);
      final int stackAmount = slotItem.getAmount();
      final int all = avarionPlayer.getAmountInInventory(slotItem);
      packetItemBuilder.lore("", "§eLinksklick: §fVerkaufe §e1 §ffür §e" + price + " " + EconomyAccount.MONEY_PLURAL);
      packetItemBuilder
          .lore("§eRechtsklick: §fVerkaufe §eStack §ffür §e" + (stackAmount * price) + " " + EconomyAccount.MONEY_PLURAL);
      packetItemBuilder.lore("§eShift + Klick: §fVerkaufe §eAlle §ffür §e" + (all * price) + " " + EconomyAccount.MONEY_PLURAL);
    } else {
      packetItemBuilder.clearLore();
      packetItemBuilder.name("§cDieses Item kann hier nicht verkauft werden.");
    }
  }
}
