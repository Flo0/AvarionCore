package com.gestankbratwurst.avarioncore.data;

import com.gestankbratwurst.avarioncore.economy.EconomyAccount;
import com.gestankbratwurst.avarioncore.friends.FriendAccount;
import com.gestankbratwurst.avarioncore.friends.FriendsMainGUI;
import com.gestankbratwurst.avarioncore.util.Msg;
import com.gestankbratwurst.avarioncore.util.Msg.Pack;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
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

  public AvarionPlayer(UUID playerID) {
    this.playerID = playerID;
    this.lastInstanceTime = System.currentTimeMillis();
    this.economyAccount = new EconomyAccount();
    this.friendAccount = new FriendAccount(this);
    this.messages = new ArrayList<>();
  }

  public AvarionPlayer(JsonObject jsonObject) {
    this.playerID = UUID.fromString(jsonObject.get("PlayerID").getAsString());
    this.lastInstanceTime = System.currentTimeMillis();
    this.economyAccount = new EconomyAccount(jsonObject.get("EconomyAccount").getAsJsonObject());
    this.friendAccount = new FriendAccount(jsonObject.get("FriendAccount").getAsJsonObject(), this);
    this.messages = new ArrayList<>();
    for (JsonElement element : jsonObject.get("Messages").getAsJsonArray()) {
      messages.add(new Msg.Pack(element.getAsJsonObject()));
    }
  }

  @Getter
  private final UUID playerID;
  @Getter
  private final long lastInstanceTime;
  @Getter
  private final EconomyAccount economyAccount;
  @Getter
  private final FriendAccount friendAccount;
  private final List<Msg.Pack> messages;

  public void openFriendGUI() {
    FriendsMainGUI.open(this);
  }

  public void onLogin(PlayerLoginEvent event) {
    for (Msg.Pack msgPack : messages) {
      Msg.send(event.getPlayer(), msgPack.getModuleName(), msgPack.getMessage());
    }
    messages.clear();
  }

  @Nullable
  public Player getPlayer() {
    return Bukkit.getPlayer(playerID);
  }

  public void sendMessage(String module, String message) {
    this.sendMessage(module, message, false);
  }

  public void sendMessage(String module, String message, boolean persistent) {
    Player player = getPlayer();
    if (player != null) {
      Msg.send(player, module, message);
    } else if (persistent) {
      messages.add(new Msg.Pack(module, message));
    }
  }

  public JsonObject getAsJson() {
    JsonObject json = new JsonObject();
    json.addProperty("PlayerID", playerID.toString());
    json.addProperty("LastInstanceTime", lastInstanceTime);
    json.add("EconomyAccount", economyAccount.getAsJson());
    json.add("FriendAccount", friendAccount.getAsJson());

    JsonArray messageArray = new JsonArray();

    for (Msg.Pack msgPack : messages) {
      messageArray.add(msgPack.getAsJson());
    }

    json.add("Messages", messageArray);

    return json;
  }

}
