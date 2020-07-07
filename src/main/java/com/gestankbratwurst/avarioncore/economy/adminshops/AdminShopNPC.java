package com.gestankbratwurst.avarioncore.economy.adminshops;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.util.Msg;
import com.gestankbratwurst.avarioncore.util.common.UtilLoc;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.crytec.libs.protocol.npc.types.NPCVillager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 08.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class AdminShopNPC extends NPCVillager {

  public AdminShopNPC(final Location location, final String displayName, final AdminShop adminShop) {
    super(location);
    super.setDisplayname(displayName);
    this.adminShop = adminShop;
    this.setProfession(Profession.NONE);
    this.setType(Type.PLAINS);
  }

  public AdminShopNPC(final JsonObject jsonObject) {
    super(UtilLoc.locFromString(jsonObject.get("Location").getAsString()));
    this.setProfession(Villager.Profession.valueOf(jsonObject.get("Profession").getAsString()));
    this.setType(Villager.Type.valueOf(jsonObject.get("Type").getAsString()));
    final AdminShopManager adminShopManager = AvarionCore.getInstance().getEconomyManager().getAdminShopManager();
    this.adminShop = adminShopManager.getAdminShop(jsonObject.get("AdminShop").getAsString());
  }

  @Getter
  private final AdminShop adminShop;
  private Villager.Profession profession;
  private Villager.Type type;

  @Override
  public void onPlayerInteraction(final Player player) {
    if (this.adminShop == null) {
      Msg.error(player, "Shops", "Dieser NPC ist mit keinem Shop verlinkt und wird nach dem nächsten Neustart gelöscht.");
      return;
    }
    this.adminShop.openShopView(player);
  }

  @Override
  public void setType(final Villager.Type type) {
    this.type = type;
    super.setType(type);
  }

  @Override
  public void setProfession(final Villager.Profession profession) {
    this.profession = profession;
    super.setProfession(profession);
  }

  public JsonObject getAsJson() {
    final JsonObject jsonObject = new JsonObject();
    if (this.adminShop == null) {
      return null;
    }
    jsonObject.addProperty("Location", UtilLoc.locToString(this.getLocation()));
    jsonObject.addProperty("Type", this.type.toString());
    jsonObject.addProperty("Profession", this.profession.toString());
    jsonObject.addProperty("AdminShop", this.adminShop.getShopTitle());

    return jsonObject;
  }

}
