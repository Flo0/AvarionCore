package com.gestankbratwurst.avarioncore.friends;

import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.util.Msg;
import com.gestankbratwurst.avarioncore.util.OfflinePlayerCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 30.06.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class FriendAccount {

  public FriendAccount(JsonObject jsonObject, AvarionPlayer holder) {
    this.receivedRequests = new ArrayList<>();
    this.friendList = new LinkedHashSet<>();
    this.holder = holder;

    JsonArray receivedArray = jsonObject.get("ReceivedFriendRequests").getAsJsonArray();
    JsonArray friendArray = jsonObject.get("FriendList").getAsJsonArray();

    for (JsonElement element : receivedArray) {
      receivedRequests.add(new FriendRequest(element.getAsJsonObject()));
    }

    for (JsonElement element : friendArray) {
      addFriendID(UUID.fromString(element.getAsString()));
    }

    for (UUID playerID : friendList) {
      OfflinePlayerCache.load(playerID);
    }

    for (FriendRequest request : receivedRequests) {
      OfflinePlayerCache.load(request.getInquirerID());
    }
  }

  public FriendAccount(AvarionPlayer holder) {
    this.receivedRequests = new ArrayList<>();
    this.friendList = new LinkedHashSet<>();
    this.holder = holder;
  }

  @Getter
  private final AvarionPlayer holder;
  private final List<FriendRequest> receivedRequests;
  private final Set<UUID> friendList;

  public int getRequestCount() {
    return receivedRequests.size();
  }

  public boolean isFriend(UUID playerID) {
    return friendList.contains(playerID);
  }

  public List<UUID> listFriends() {
    return new ArrayList<>(friendList);
  }

  public List<FriendRequest> listReceivedRequests() {
    return new ArrayList<>(receivedRequests);
  }

  public void sendRequest(AvarionPlayer other) {
    Player player = holder.getPlayer();
    if (player == null) {
      throw new IllegalStateException("Player must be online in order to send friend requests.");
    }

    for (FriendRequest received : this.receivedRequests) {
      if (received.getInquirerID().equals(other.getPlayerID())) {
        received.accept(this.holder);
        return;
      }
    }

    String senderName = Msg.elem(player.getName());
    FriendRequest request = new FriendRequest(other.getPlayerID(), player.getUniqueId(), player.getName());

    other.getFriendAccount().addFriendRequest(request);
    other.sendMessage("Freunde", "Du hast eine Freundschaftsanfrage von " + senderName + " erhalten.", true);
  }

  public void addFriendID(UUID playerID) {
    friendList.add(playerID);
  }

  public void removeFriendID(UUID playerID) {
    friendList.add(playerID);
  }

  public void addFriendRequest(FriendRequest request) {
    receivedRequests.add(request);
  }

  public void removeFriendRequest(FriendRequest request) {
    receivedRequests.remove(request);
  }

  public JsonObject getAsJson() {
    JsonObject jsonObject = new JsonObject();

    JsonArray receivedRequestArray = new JsonArray();
    JsonArray friendArray = new JsonArray();

    for (FriendRequest request : receivedRequests) {
      receivedRequestArray.add(request.getAsJson());
    }

    for (UUID friendID : friendList) {
      friendArray.add(friendID.toString());
    }

    jsonObject.add("ReceivedFriendRequests", receivedRequestArray);
    jsonObject.add("FriendList", friendArray);

    return jsonObject;
  }

}
