package com.gestankbratwurst.avarioncore.data;

import com.gestankbratwurst.avarioncore.economy.EconomyAccount;
import com.gestankbratwurst.avarioncore.friends.FriendAccount;
import com.gestankbratwurst.avarioncore.friends.FriendsMainGUI;
import com.gestankbratwurst.avarioncore.util.Msg;
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

  public AvarionPlayer(final UUID playerID) {
    this.playerID = playerID;
    this.lastInstanceTime = System.currentTimeMillis();
    this.economyAccount = new EconomyAccount();
    this.friendAccount = new FriendAccount(this);
    this.messages = new ArrayList<>();
  }

  public AvarionPlayer(final JsonObject jsonObject) {
    this.playerID = UUID.fromString(jsonObject.get("PlayerID").getAsString());
    this.lastInstanceTime = System.currentTimeMillis();
    this.economyAccount = new EconomyAccount(jsonObject.get("EconomyAccount").getAsJsonObject());
    this.friendAccount = new FriendAccount(jsonObject.get("FriendAccount").getAsJsonObject(), this);
    this.messages = new ArrayList<>();
    for (final JsonElement element : jsonObject.get("Messages").getAsJsonArray()) {
      this.messages.add(new Msg.Pack(element.getAsJsonObject()));
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

  public void onLogin(final PlayerLoginEvent event) {
    for (final Msg.Pack msgPack : this.messages) {
      Msg.send(event.getPlayer(), msgPack.getModuleName(), msgPack.getMessage());
    }
    this.messages.clear();
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
      this.messages.add(new Msg.Pack(module, message));
    }
  }

  public JsonObject getAsJson() {
    final JsonObject json = new JsonObject();
    json.addProperty("PlayerID", this.playerID.toString());
    json.addProperty("LastInstanceTime", this.lastInstanceTime);
    json.add("EconomyAccount", this.economyAccount.getAsJson());
    json.add("FriendAccount", this.friendAccount.getAsJson());

    final JsonArray messageArray = new JsonArray();

    for (final Msg.Pack msgPack : this.messages) {
      messageArray.add(msgPack.getAsJson());
    }

    json.add("Messages", messageArray);

    return json;
  }

}
