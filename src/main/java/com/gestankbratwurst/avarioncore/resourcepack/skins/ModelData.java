package com.gestankbratwurst.avarioncore.resourcepack.skins;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 25.11.2019
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@Data
@AllArgsConstructor
public class ModelData {

  private final String modelParent;
  private final JsonObject displayJson;
  private final JsonObject elementsJson;

  public static ModelData of(final String modelParent, final JsonObject displayJson, final JsonObject elementsJson) {
    return new ModelData(modelParent, displayJson, elementsJson);
  }

  public static ModelData defaultGenerated() {
    return ModelData.of("item/generated", null, null);
  }

  public static ModelData defaultHandheld() {
    return ModelData.of("item/handheld", null, null);
  }

  public static ModelData handheldScaled(double scale, double dx, double dy, double dz) {
    String parent = "item/handheld";
    JsonObject displayJson = new JsonObject();
    JsonObject handScaleJson = new JsonObject();
    JsonArray translationArray = new JsonArray();
    JsonArray scaleArray = new JsonArray();
    translationArray.add(dx);
    translationArray.add(dy);
    translationArray.add(dz);
    scaleArray.add(scale);
    scaleArray.add(scale);
    scaleArray.add(scale);
    JsonArray guiArray = new JsonArray();
    JsonArray guiArrayTrans = new JsonArray();
    guiArray.add(scale);
    guiArray.add(scale);
    guiArray.add(scale);
    guiArrayTrans.add(scale);
    guiArrayTrans.add(scale);
    guiArrayTrans.add(scale);
    JsonObject guiJson = new JsonObject();
    guiJson.add("scale", guiArray);
    guiJson.add("translation", guiArrayTrans);
    handScaleJson.add("translation", translationArray);
    handScaleJson.add("scale", scaleArray);
    displayJson.add("thirdperson_righthand", handScaleJson);
    displayJson.add("thirdperson_lefthand", handScaleJson);
    displayJson.add("firstperson_lefthand", handScaleJson);
    displayJson.add("firstperson_righthand", handScaleJson);
    displayJson.add("gui", guiJson);
    return ModelData.of(parent, displayJson, null);
  }

}