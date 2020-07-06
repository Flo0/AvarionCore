package com.gestankbratwurst.avarioncore.util.common;

import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.gestankbratwurst.avarioncore.util.nbtapi.NBTItem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import net.minecraft.server.v1_16_R1.MojangsonParser;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 07.04.2020
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class UtilItem implements Listener {

  public static void init(final JavaPlugin plugin) {
    Bukkit.getPluginManager().registerEvents(new UtilItem(), plugin);
  }

  public static char asChar(final ItemStack item) {
    final NBTItem nbt = new NBTItem(item);
    if (nbt.hasKey("Model")) {
      return Model.valueOf(nbt.getString("Model")).getChar();
    }
    return 'X';
  }

  public static String serialize(final ItemStack[] items) {
    try {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

      // Write the size of the inventory
      dataOutput.writeInt(items.length);

      // Save every element in the list
      for (int i = 0; i < items.length; i++) {
        dataOutput.writeObject(items[i]);
      }

      // Serialize that array
      dataOutput.close();
      return Base64Coder.encodeLines(outputStream.toByteArray());
    } catch (final Exception e) {
      throw new IllegalStateException("Unable to save item stacks.", e);
    }
  }

  public static String serialize(final ItemStack itemStack) {
    final NBTTagCompound tag = new NBTTagCompound();
    CraftItemStack.asNMSCopy(itemStack).save(tag);
    return tag.toString();
  }

  public static ItemStack deserializeItemStack(final String string) {
    if (string == null || string.equals("empty")) {
      return null;
    }
    try {
      final NBTTagCompound comp = MojangsonParser.parse(string);
      final net.minecraft.server.v1_16_R1.ItemStack cis = net.minecraft.server.v1_16_R1.ItemStack.a(comp);
      return CraftItemStack.asBukkitCopy(cis);
    } catch (final CommandSyntaxException ex) {
      ex.printStackTrace();
    }
    return null;
  }


  public static ItemStack[] deserialize(final String data) {
    try {
      final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
      final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
      final ItemStack[] items = new ItemStack[dataInput.readInt()];

      // Read the serialized inventory
      for (int i = 0; i < items.length; i++) {
        items[i] = (ItemStack) dataInput.readObject();
      }

      dataInput.close();
      return items;
    } catch (final ClassNotFoundException | IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static boolean isEnachantable(final ItemStack item) {
    if (!item.getEnchantments().isEmpty()) {
      return false;
    }
    final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
    return !container.has(NameSpaceFactory.provide("ENCHANTBLOCK"), PersistentDataType.INTEGER);
  }

  public static boolean canReceiveAnvil(final ItemStack item) {
    final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
    return !container.has(NameSpaceFactory.provide("BLOCKANVILRECEIVER"), PersistentDataType.INTEGER);
  }

  public static boolean canProvideAnvil(final ItemStack item) {
    final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
    return !container.has(NameSpaceFactory.provide("BLOCKANVILPROVIDER"), PersistentDataType.INTEGER);
  }

  private UtilItem() {

  }

  @EventHandler
  public void onEnchant(final PrepareItemEnchantEvent event) {
    if (isEnachantable(event.getItem())) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onAnvil(final PrepareAnvilEvent event) {
    final ItemStack left = event.getInventory().getItem(0);
    final ItemStack right = event.getInventory().getItem(1);
    if (left == null || right == null || left.getType() == Material.AIR || right.getType() == Material.AIR) {
      return;
    }
    if (!canReceiveAnvil(left) || !canProvideAnvil(right)) {
      event.setResult(null);
    }
  }

}
