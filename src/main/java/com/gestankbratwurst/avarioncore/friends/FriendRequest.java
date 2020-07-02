package com.gestankbratwurst.avarioncore.friends;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionDataManager;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.util.Msg;
import com.google.gson.JsonObject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
public class FriendRequest {

  public FriendRequest(UUID receiverID, UUID inquirerID, String inquirerName) {
    this.inquirerID = inquirerID;
    this.inquirerName = inquirerName;
    this.receiverID = receiverID;
  }

  public FriendRequest(JsonObject jsonObject) {
    this.inquirerID = UUID.fromString(jsonObject.get("InquirerID").getAsString());
    this.inquirerName = jsonObject.get("InquirerName").getAsString();
    this.receiverID = UUID.fromString(jsonObject.get("ReceiverID").getAsString());
  }

  @Getter
  private final UUID receiverID;
  @Getter
  private final UUID inquirerID;
  @Getter
  private final String inquirerName;

  public void accept(AvarionPlayer receiverAvPlayer) {
    AvarionDataManager dataManager = AvarionCore.getInstance().getAvarionDataManager();
    CompletableFuture<AvarionPlayer> senderFuture = dataManager.getData(inquirerID);

    final Player receiver = receiverAvPlayer.getPlayer();

    if (receiver == null) {
      throw new IllegalStateException("Player needs to be online in order to accept friend requests.");
    }

    final UUID receiverID = receiver.getUniqueId();
    String receiverName = Msg.elem(receiver.getName());

    senderFuture.thenAccept(sender -> {
      sender.sendMessage("Freunde", "Deine Freundschaftsanfrage wurde von " + receiverName + " angenommen.", true);
      sender.getFriendAccount().addFriendID(receiverID);
    });

    String inqName = Msg.elem(inquirerName);

    receiverAvPlayer.sendMessage("Freunde", "Du hast die Freundschaftsanfrage von " + inqName + " angenommen.");
    receiverAvPlayer.getFriendAccount().removeFriendRequest(this);
    receiverAvPlayer.getFriendAccount().addFriendID(inquirerID);
  }

  public void decline(AvarionPlayer receiverAvPlayer) {
    AvarionDataManager dataManager = AvarionCore.getInstance().getAvarionDataManager();
    CompletableFuture<AvarionPlayer> senderFuture = dataManager.getData(inquirerID);

    Player receiver = receiverAvPlayer.getPlayer();

    if (receiver == null) {
      throw new IllegalStateException("Player needs to be online in order to decline friend requests.");
    }

    String receiverName = Msg.elem(receiver.getName());

    senderFuture.thenAccept(sender -> {
      sender.sendMessage("Freunde", "Deine Freundschaftsanfrage wurde von " + receiverName + " abgelehnt.", true);
    });

    String inqName = Msg.elem(inquirerName);

    receiverAvPlayer.sendMessage("Freunde", "Du hast die Freundschaftsanfrage von " + inqName + " abgelehnt.");
    receiverAvPlayer.getFriendAccount().removeFriendRequest(this);
  }

  public JsonObject getAsJson() {
    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("InquirerID", inquirerID.toString());
    jsonObject.addProperty("InquirerName", inquirerName);
    jsonObject.addProperty("ReceiverID", receiverID.toString());

    return jsonObject;
  }

  @Override
  public boolean equals(Object otherObj) {
    if (!(otherObj instanceof FriendRequest)) {
      return false;
    }
    FriendRequest other = (FriendRequest) otherObj;
    return other.inquirerID.equals(this.inquirerID) && other.receiverID.equals(this.receiverID);
  }

}
