package com.gestankbratwurst.avarioncore.resourcepack.packing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 24.11.2019
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class PackMeta {

  protected PackMeta(final int packFormat, final String description) {
    this.packFormat = packFormat;
    this.description = description;
    gson = new GsonBuilder().setPrettyPrinting().create();
  }

  private final Gson gson;
  private final int packFormat;
  private final String description;

  public String getAsJsonString() {
    final JsonObject json = new JsonObject();
    final JsonObject packJson = new JsonObject();
    packJson.addProperty("pack_format", packFormat);
    packJson.addProperty("description", description);
    json.add("pack", packJson);
    return gson.toJson(json);
  }

}
