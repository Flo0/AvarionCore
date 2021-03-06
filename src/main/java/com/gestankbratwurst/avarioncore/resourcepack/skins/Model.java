package com.gestankbratwurst.avarioncore.resourcepack.skins;

import com.gestankbratwurst.avarioncore.resourcepack.packing.BoxedFontChar;
import com.gestankbratwurst.avarioncore.util.items.ItemBuilder;
import com.gestankbratwurst.avarioncore.util.nbtapi.NBTItem;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.File;
import java.lang.reflect.Field;
import lombok.Getter;
import lombok.Setter;
import net.crytec.libs.protocol.skinclient.data.Skin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 24.11.2019
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public enum Model {

  BLACK_ARROW_DOWN(Material.STICK, 1000, false, false),
  BLACK_ARROW_LEFT(Material.STICK, 1001, false, false),
  BLACK_ARROW_RIGHT(Material.STICK, 1002, false, false),
  BLACK_ARROW_UP(Material.STICK, 1003, false, false),
  GREEN_CHECK(Material.STICK, 1004, false, false),
  RED_X(Material.STICK, 1005, false, false),
  DOUBLE_GRAY_ARROW_UP(Material.STICK, 1008, false, false),
  DOUBLE_GRAY_ARROW_DOWN(Material.STICK, 1009, false, false),
  DOUBLE_RED_ARROW_UP(Material.STICK, 1010, false, false),
  DOUBLE_GREEN_ARROW_DOWN(Material.STICK, 1011, false, false),
  GREEN_PLUS(Material.STICK, 1012, false, false),
  SCROLL(Material.STICK, 1013, false, false),
  GOLD_PILE_TINY(Material.STICK, 1100, false, true),
  GOLD_PILE_SMALL(Material.STICK, 1101, false, true),
  GOLD_PILE_MEDIUM(Material.STICK, 1102, false, true),
  GOLD_PILE_BIG(Material.STICK, 1103, false, true),
  GOLD_PILE_HUGE(Material.STICK, 1104, false, true),
  GOLD_PILE_BAR_SMALL(Material.STICK, 1105, false, true),
  GOLD_PILE_BAR_MEDIUM(Material.STICK, 1106, false, true),
  GOLD_PILE_BAR_BIG(Material.STICK, 1107, false, true),
  SILVER_PILE_TINY(Material.STICK, 1108, false, true),
  SILVER_PILE_SMALL(Material.STICK, 1109, false, true),
  SILVER_PILE_MEDIUM(Material.STICK, 1110, false, true),
  SILVER_PILE_BIG(Material.STICK, 1111, false, true),
  SILVER_PILE_HUGE(Material.STICK, 1112, false, true),
  COPPER_PILE_TINY(Material.STICK, 1113, false, true),
  COPPER_PILE_SMALL(Material.STICK, 1114, false, true),
  COPPER_PILE_MEDIUM(Material.STICK, 1115, false, true),
  COPPER_PILE_BIG(Material.STICK, 1116, false, true),
  COPPER_PILE_HUGE(Material.STICK, 1117, false, true),
  COINS_GOLD_0(Material.STICK, 1118, false, false),
  COINS_GOLD_1(Material.STICK, 1119, false, false),
  COINS_GOLD_2(Material.STICK, 1120, false, false),
  COINS_GOLD_3(Material.STICK, 1121, false, false),
  COINS_GOLD_4(Material.STICK, 1122, false, false),
  COINS_GOLD_5(Material.STICK, 1123, false, false),
  COINS_SILVER_0(Material.STICK, 1124, false, false),
  COINS_SILVER_1(Material.STICK, 1125, false, false),
  COINS_SILVER_2(Material.STICK, 1126, false, false),
  COINS_SILVER_3(Material.STICK, 1127, false, false),
  COINS_SILVER_4(Material.STICK, 1128, false, false),
  COINS_SILVER_5(Material.STICK, 1129, false, false),
  COINS_COPPER_0(Material.STICK, 1130, false, false),
  COINS_COPPER_1(Material.STICK, 1131, false, false),
  COINS_COPPER_2(Material.STICK, 1132, false, false),
  COINS_COPPER_3(Material.STICK, 1133, false, false),
  COINS_COPPER_4(Material.STICK, 1134, false, false),
  COINS_COPPER_5(Material.STICK, 1135, false, false),
  BARS_GOLD_0(Material.STICK, 1136, false, false),
  BARS_GOLD_1(Material.STICK, 1137, false, false),
  BARS_GOLD_2(Material.STICK, 1138, false, false);

  Model(final Material baseMaterial, final int modelID, final boolean headEnabled, final boolean customModelDataEnabled) {
    this.baseMaterial = baseMaterial;
    this.modelID = modelID;
    this.modelData = ModelData.defaultGenerated();
    this.fontMeta = FontMeta.common();
    this.boxedFontChar = new BoxedFontChar();
    headSkinEnabled = headEnabled;
    this.customModelDataEnabled = customModelDataEnabled;
  }

  Model(final Material baseMaterial, final int modelID, final ModelData modelData, final FontMeta fontMeta, final boolean headEnabled, final boolean customModelDataEnabled) {
    this.baseMaterial = baseMaterial;
    this.modelID = modelID;
    this.modelData = modelData;
    this.fontMeta = fontMeta;
    this.boxedFontChar = new BoxedFontChar();
    headSkinEnabled = headEnabled;
    this.customModelDataEnabled = customModelDataEnabled;
  }

  @Getter
  private final Material baseMaterial;
  @Getter
  private final int modelID;
  @Getter
  private final ModelData modelData;
  @Getter
  private final FontMeta fontMeta;
  @Getter
  private final BoxedFontChar boxedFontChar;
  @Getter
  private final boolean headSkinEnabled;
  @Getter
  private final boolean customModelDataEnabled;
  @Getter
  @Setter
  private Skin skin;
  @Getter
  @Setter
  private File linkedImageFile;
  @Getter
  private GameProfile gameProfile;

  private ItemStack head;

  private ItemStack item;

  private void initProfile() {
    if (gameProfile == null && skin != null) {
      gameProfile = new GameProfile(skin.data.uuid, skin.name);
      gameProfile.getProperties().put("textures", new Property("textures", skin.data.texture.value, skin.data.texture.signature));
    }
  }

  public char getChar() {
    return boxedFontChar.getAsCharacter();
  }

  public ItemStack getItem() {
    if (item == null) {
      item = new ItemBuilder(baseMaterial)
          .modelData(modelID)
          .name(this.toString())
          .build();
      NBTItem nbt = new NBTItem(item);
      nbt.setString("Model", this.toString());
      item = nbt.getItem();
    }
    return item.clone();
  }

  public ItemStack getHead() {
    if (head != null) {
      return head.clone();
    }
    initProfile();
    final ItemStack newHead = new ItemStack(Material.PLAYER_HEAD);
    final SkullMeta headMeta = (SkullMeta) newHead.getItemMeta();
    Field profileField;

    try {
      profileField = headMeta.getClass().getDeclaredField("profile");
      profileField.setAccessible(true);
      profileField.set(headMeta, gameProfile);
    } catch (final NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
      e1.printStackTrace();
    }

    newHead.setItemMeta(headMeta);
    NBTItem nbt = new NBTItem(newHead);
    nbt.setString("Model", this.toString());
    head = nbt.getItem();
    return head.clone();
  }

}