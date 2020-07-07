package com.gestankbratwurst.avarioncore.economy.adminshops.guis;

import com.gestankbratwurst.avarioncore.economy.adminshops.AdminShop;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
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
public class AdminShopEditorProvider implements InventoryProvider {


  public AdminShopEditorProvider(final AdminShop adminShop) {
    this.adminShop = adminShop;
  }

  private final AdminShop adminShop;

  @Override
  public void init(final Player player, final InventoryContent content) {
    
  }

  @Override
  public void onClose(final Player player, final InventoryContent content) {

  }

}
