package com.gestankbratwurst.avarioncore.protection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.EnumMap;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 01.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class RuleSet {

  public RuleSet(JsonObject jsonObject) {
    this.globalContext = jsonObject.get("GlobalContext").getAsBoolean();
    this.ruleStates = new EnumMap<>(ProtectionRule.class);
    JsonObject rulesJson = jsonObject.get("Rules").getAsJsonObject();
    for (Entry<String, JsonElement> entry : rulesJson.entrySet()) {
      this.setState(ProtectionRule.valueOf(entry.getKey()), RuleState.valueOf(entry.getValue().getAsString()));
    }
  }

  public RuleSet(boolean globalContext) {
    this.globalContext = globalContext;
    this.ruleStates = new EnumMap<>(ProtectionRule.class);
    for (ProtectionRule rule : ProtectionRule.values()) {
      if (globalContext == rule.isGlobalContext()) {
        this.setState(rule, rule.getDefaultState());
      }
    }
  }

  private final EnumMap<ProtectionRule, RuleState> ruleStates;
  private final boolean globalContext;

  public void setState(ProtectionRule rule, RuleState state) {
    if (rule.isGlobalContext() != this.globalContext) {
      throw new IllegalArgumentException("Tried to add rule with different context.");
    }
    ruleStates.put(rule, state);
  }

  @Nullable
  public RuleState getState(ProtectionRule rule) {
    if (rule.isGlobalContext() != this.globalContext) {
      throw new IllegalArgumentException("Tried to get rule with different context.");
    }
    return ruleStates.get(rule);
  }

  public JsonObject getAsJson() {
    JsonObject jsonObject = new JsonObject();
    JsonObject rulesJson = new JsonObject();
    for (Entry<ProtectionRule, RuleState> ruleEntry : ruleStates.entrySet()) {
      rulesJson.addProperty(ruleEntry.getKey().toString(), ruleEntry.getValue().toString());
    }
    jsonObject.add("Rules", rulesJson);

    jsonObject.addProperty("GlobalContext", globalContext);

    return jsonObject;
  }

}