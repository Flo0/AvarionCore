package com.gestankbratwurst.avarioncore.resourcepack.packing;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 24.11.2019
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class AssetLibrary {

  private static final String ASSET_URL = "https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/1.14.4/assets/minecraft/models/";

  protected AssetLibrary(final AvarionCore plugin) {
    this.plugin = plugin;
    itemModelDefaultAssets = Maps.newHashMap();
    iterateAssets();
  }

  private final AvarionCore plugin;
  private final Map<String, JsonObject> itemModelDefaultAssets;

  private void iterateAssets() {

    for (final Model model : Model.values()) {
      final String modelFolder = model.getBaseMaterial().isBlock() ? "block" : "item";
      final String nmsName = model.getBaseMaterial().getKey().getKey();
      if (!itemModelDefaultAssets.containsKey(nmsName)) {
        final String jsonUrl = ASSET_URL + modelFolder + "/" + nmsName + ".json";
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String request = jsonGetRequest(jsonUrl);
        if (request == null) {
          return;
        }
        final JsonObject json = gson.fromJson(request, JsonObject.class);
        itemModelDefaultAssets.put(nmsName, json);
      }
    }

  }

  private String jsonGetRequest(final String urlQueryString) {
    String json = null;
    try {
      final URL url = new URL(urlQueryString);
      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("charset", "utf-8");
      connection.connect();
      final InputStream inStream = connection.getInputStream();
      final InputStreamReader isr = new InputStreamReader(inStream, "UTF-8");
      int read;
      final StringBuilder builder = new StringBuilder();
      while ((read = isr.read()) != -1) {
        builder.append((char) read);
      }
      json = builder.toString();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
    return json;
  }

  protected String getAssetModelParent(final String nmsKey) {
    return itemModelDefaultAssets.get(nmsKey).get("parent").getAsString();
  }

  protected String getAssetModelLayer0(final String nmsKey) {
    return itemModelDefaultAssets.get(nmsKey)
        .get("textures")
        .getAsJsonObject()
        .get("layer0").getAsString();
  }

}
