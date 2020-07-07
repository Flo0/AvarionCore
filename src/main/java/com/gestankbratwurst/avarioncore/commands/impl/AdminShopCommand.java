package com.gestankbratwurst.avarioncore.commands.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.gestankbratwurst.avarioncore.economy.adminshops.AdminShop;
import com.gestankbratwurst.avarioncore.economy.adminshops.AdminShopManager;
import com.gestankbratwurst.avarioncore.economy.adminshops.ShopType;
import com.gestankbratwurst.avarioncore.util.Msg;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 07.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@CommandAlias("adminshop")
@CommandPermission("admin")
@RequiredArgsConstructor
public class AdminShopCommand extends BaseCommand {

  private final AdminShopManager adminShopManager;

  @Subcommand("create")
  @CommandCompletion("@ShopType @ShopName")
  public void onCreate(final Player player, final ShopType shopType, final String name) {
    this.adminShopManager.createShop(name, shopType);
  }

  @Subcommand("delete")
  @CommandCompletion("@ShopName")
  public void onDelete(final Player player, final String name) {
    this.adminShopManager.deleteShop(name);
  }

  @Subcommand("edit")
  @CommandCompletion("@ShopName")
  public void onEdit(final Player player, final String name) {
    final AdminShop adminShop = this.adminShopManager.getAdminShop(name);
    if (adminShop == null) {
      Msg.error(player, "Shops", "Dieser Shop existiert nicht.");
      return;
    }
    adminShop.openEditor(player);

    Msg.send(player, "Shops", "Shop wurde zum editieren geöffnet.");
  }

  @Subcommand("open")
  @CommandCompletion("@ShopName")
  public void onOpen(final Player player, final String name) {
    final AdminShop adminShop = this.adminShopManager.getAdminShop(name);
    if (adminShop == null) {
      Msg.error(player, "Shops", "Dieser Shop existiert nicht.");
      return;
    }

    adminShop.openShopView(player);
    Msg.send(player, "Shops", "Shop wurde geöffnet.");
  }

}
