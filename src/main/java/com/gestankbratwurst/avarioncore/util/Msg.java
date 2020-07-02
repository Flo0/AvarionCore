package com.gestankbratwurst.avarioncore.util;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LaLaLand-CorePlugin and was created at the 16.11.2019
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class Msg {

  public static void init(final AvarionCore plugin) {
    corePlugin = plugin;
  }

  private static AvarionCore corePlugin;

  private static final String MODULE_COLOR = "§9";
  private static final String ERROR_COLOR = "§4";
  private static final String MESSAGE_COLOR = "§7";
  private static final String ELEMENT_COLOR = "§e";

  /**
   * Used to send a message to any player.
   *
   * @param player  the player
   * @param module  the prefix
   * @param message the message
   */
  public static void send(final Player player, final String module, final String message) {
    player.sendMessage(MODULE_COLOR + module + "> " + MESSAGE_COLOR + message);
  }

  /**
   * Used to format elements.
   *
   * @param input the input element.
   * @return a formated element.
   */
  public static String elem(final String input) {
    return ELEMENT_COLOR + input + MESSAGE_COLOR;
  }

  /**
   * Used to send error message to player.
   *
   * @param player  the player
   * @param module  the prefix
   * @param message the message
   */
  public static void error(final Player player, final String module, final String message) {
    player.sendMessage(ERROR_COLOR + module + "> " + MESSAGE_COLOR + message);
  }

  public static class Pack {

    public Pack(String moduleName, String message) {
      this.moduleName = moduleName;
      this.message = message;
    }

    public Pack(JsonObject jsonObject) {
      this.moduleName = jsonObject.get("ModuleName").getAsString();
      this.message = jsonObject.get("Message").getAsString();
    }

    @Getter
    private final String moduleName;
    @Getter
    private final String message;

    public JsonObject getAsJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("ModuleName", moduleName);
      jsonObject.addProperty("Message", message);
      return jsonObject;
    }

  }

}
