package com.gestankbratwurst.avarioncore.data;

import com.gestankbratwurst.avarioncore.economy.EconomyAccount;
import com.gestankbratwurst.avarioncore.friends.FriendAccount;
import com.gestankbratwurst.avarioncore.friends.FriendsMainGUI;
import com.gestankbratwurst.avarioncore.util.Msg;
import com.gestankbratwurst.avarioncore.util.common.UtilItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 30.06.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class AvarionPlayer {

  public AvarionPlayer(final UUID playerID) {
    this.playerID = playerID;
    this.lastInstanceTime = System.currentTimeMillis();
    this.economyAccount = new EconomyAccount();
    this.friendAccount = new FriendAccount(this);
    this.messages = new ArrayList<>();
    this.itemQueue = new ArrayDeque<>();
    this.inventory = Bukkit.createInventory(null, 5 * 9);
  }

  public AvarionPlayer(final JsonObject jsonObject) {
    this.playerID = UUID.fromString(jsonObject.get("PlayerID").getAsString());
    this.lastInstanceTime = System.currentTimeMillis();
    this.itemQueue = new ArrayDeque<>();
    this.economyAccount = new EconomyAccount(jsonObject.get("EconomyAccount").getAsJsonObject());
    this.friendAccount = new FriendAccount(jsonObject.get("FriendAccount").getAsJsonObject(), this);
    this.lastSeenName = jsonObject.get("LastSeenName").getAsString();
    this.messages = new ArrayList<>();
    for (final JsonElement element : jsonObject.get("Messages").getAsJsonArray()) {
      this.messages.add(new Msg.Pack(element.getAsJsonObject()));
    }
    final ItemStack[] items = UtilItem.deserialize(jsonObject.get("ItemQueue").getAsString());
    this.itemQueue.addAll(Arrays.asList(items));
    this.inventory = Bukkit.createInventory(null, 5 * 9);
    this.inventory.setContents(UtilItem.deserialize(jsonObject.get("Inventory").getAsString()));
  }

  @Getter
  private final UUID playerID;
  @Getter
  private final long lastInstanceTime;
  @Getter
  private final EconomyAccount economyAccount;
  @Getter
  private final FriendAccount friendAccount;
  @Getter
  private String lastSeenName = "NONE";
  private final List<Msg.Pack> messages;
  private final ArrayDeque<ItemStack> itemQueue;
  private final Inventory inventory;


  public void openFriendGUI() {
    FriendsMainGUI.open(this);
  }

  private void queueItem(final ItemStack item) {
    item.add();
  }

  public void giveItem(final ItemStack item, final boolean dropOnFull) {
    final Player player = this.getPlayer();
    if (player != null) {
      player.getInventory().addItem(item).values().forEach(left -> {
        if (dropOnFull) {
          player.getWorld().dropItemNaturally(player.getLocation(), left);
        } else {
          this.queueItem(left);
        }
      });
    } else {
      this.inventory.addItem(item).values().forEach(this::queueItem);
    }
  }

  public void giveItems(final ItemStack item, final int finalAmount, final boolean dropOnFull) {
    final int maxStackSize = item.getMaxStackSize();
    final int fullStacks = finalAmount / maxStackSize;
    final int left = finalAmount % maxStackSize;

    final ItemStack stack = new ItemStack(item.clone());
    for (int i = 0; i < fullStacks; i++) {
      this.giveItem(stack.clone(), dropOnFull);
    }

    if (left > 0) {
      stack.setAmount(left);
      this.giveItem(stack.clone(), dropOnFull);
    }
  }

  public void onQuit(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    this.inventory.setContents(player.getInventory().getContents());
  }

  public void onLogin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    for (final Msg.Pack msgPack : this.messages) {
      Msg.send(player, msgPack.getModuleName(), msgPack.getMessage());
    }
    this.messages.clear();
    this.lastSeenName = player.getName();
    player.getInventory().setContents(Arrays.copyOf(this.inventory.getContents(), 40));
    if (!this.itemQueue.isEmpty()) {
      final int amount = this.itemQueue.size();
      this.sendMessage("Items", "Du hast " + Msg.elem("" + amount) + " neue Items erhalten.");
      while (!this.itemQueue.isEmpty()) {
        this.giveItem(this.itemQueue.poll(), true);
      }
    }
  }

  @Nullable
  public Player getPlayer() {
    return Bukkit.getPlayer(this.playerID);
  }

  public void sendMessage(final String module, final String message) {
    this.sendMessage(module, message, false);
  }

  public void sendMessage(final String module, final String message, final boolean persistent) {
    final Player player = this.getPlayer();
    if (player != null) {
      Msg.send(player, module, message);
    } else if (persistent) {
      System.out.println("PERSISTENT ADDED");
      this.messages.add(new Msg.Pack(module, message));
    }
  }

  public JsonObject getAsJson() {
    final JsonObject json = new JsonObject();
    json.addProperty("PlayerID", this.playerID.toString());
    json.addProperty("LastInstanceTime", this.lastInstanceTime);
    json.add("EconomyAccount", this.economyAccount.getAsJson());
    json.add("FriendAccount", this.friendAccount.getAsJson());
    json.addProperty("LastSeenName", this.lastSeenName);
    json.addProperty("ItemQueue", UtilItem.serialize(this.itemQueue.toArray(new ItemStack[0])));
    json.addProperty("Inventory", UtilItem.serialize(this.inventory.getContents()));

    final JsonArray messageArray = new JsonArray();

    for (final Msg.Pack msgPack : this.messages) {
      messageArray.add(msgPack.getAsJson());
    }

    json.add("Messages", messageArray);

    return json;
  }

}
