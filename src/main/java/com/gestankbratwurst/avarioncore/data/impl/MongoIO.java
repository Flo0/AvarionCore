package com.gestankbratwurst.avarioncore.data.impl;

import com.gestankbratwurst.avarioncore.data.AvarionIO;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import java.util.UUID;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 03.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class MongoIO implements AvarionIO {

  public MongoIO() {
    final MongoCredential credential = MongoCredential.createCredential("god", "avarion_data", "38490htgn45etz79804".toCharArray());
    final MongoClientOptions clientOptions = new MongoClientOptions.Builder().build();
    final ServerAddress address = new ServerAddress("localhost", 27017);
    final MongoClient mongoClient = new MongoClient(address, credential, clientOptions);
    final MongoDatabase database = mongoClient.getDatabase("avarion_data");
    this.playerCollection = database.getCollection("players");
    this.worldCollection = database.getCollection("worlds");
    this.playerCollection.createIndex(Indexes.hashed("PlayerID"));
    this.worldCollection.createIndex(Indexes.hashed("WorldID"));
    this.gson = new GsonBuilder().disableHtmlEscaping().create();
  }

  private final Gson gson;
  private final MongoCollection<Document> playerCollection;
  private final MongoCollection<Document> worldCollection;

  @Override
  public @Nullable JsonObject loadPlayerData(final UUID playerID) {
    final Document data = this.playerCollection.find(new Document("PlayerID", playerID.toString())).first();
    if (data == null) {
      this.playerCollection.insertOne(Document.parse(this.gson.toJson(new AvarionPlayer(playerID).getAsJson())));
      return null;
    }
    return this.gson.fromJson(data.toJson(), JsonObject.class);
  }

  @Override
  public void savePlayerData(final UUID playerID, final JsonObject jsonData) {
    this.playerCollection.findOneAndReplace(new Document("PlayerID", playerID.toString()), Document.parse(this.gson.toJson(jsonData)));
  }

  @Override
  public @Nullable JsonObject loadWorldData(final UUID worldID) {
    final Document data = this.worldCollection.find(new Document("WorldID", worldID.toString())).first();
    if (data == null) {
      return null;
    }
    return this.gson.fromJson(data.toJson(), JsonObject.class);
  }

  @Override
  public void saveWorldData(final UUID worldID, final JsonObject jsonData) {
    if (this.loadWorldData(worldID) == null) {
      this.worldCollection.insertOne(Document.parse(this.gson.toJson(jsonData)));
    } else {
      this.worldCollection.findOneAndReplace(new Document("WorldID", worldID.toString()), Document.parse(this.gson.toJson(jsonData)));
    }
  }

}