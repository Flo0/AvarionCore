package com.gestankbratwurst.avarioncore.util.nbtapi;

import java.util.Set;
import java.util.UUID;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of CorePlugin and was created at the 28.11.2019
 *
 * CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class NBTCompound implements Cloneable {

  public NBTCompound() {
    this(new NBTTagCompound());
  }

  public NBTCompound(final NBTTagCompound nmsCompound) {
    this.nmsCompound = nmsCompound;
  }

  protected final NBTTagCompound nmsCompound;

  public void setString(@NotNull final String key, @NotNull final String value) {
    nmsCompound.setString(key, value);
  }

  public void setByte(@NotNull final String key, final byte value) {
    nmsCompound.setByte(key, value);
  }

  public void setShort(@NotNull final String key, final short value) {
    nmsCompound.setShort(key, value);
  }

  public void setInt(@NotNull final String key, final int value) {
    nmsCompound.setInt(key, value);
  }

  public void setLong(@NotNull final String key, final long value) {
    nmsCompound.setLong(key, value);
  }

  public void setFloat(@NotNull final String key, final float value) {
    nmsCompound.setFloat(key, value);
  }

  public void setDouble(@NotNull final String key, final double value) {
    nmsCompound.setDouble(key, value);
  }

  public void setBoolean(@NotNull final String key, final boolean value) {
    nmsCompound.setBoolean(key, value);
  }

  public void setByteArray(@NotNull final String key, final byte[] value) {
    nmsCompound.setByteArray(key, value);
  }

  public void setIntArray(@NotNull final String key, final int[] value) {
    nmsCompound.setIntArray(key, value);
  }

  public void setUUID(@NotNull final String key, @NotNull final UUID value) {
    nmsCompound.setString(key, value.toString());
  }

  public void remove(final String key) {
    nmsCompound.remove(key);
  }

  public void addCompound(final String key, final NBTCompound compound) {
    nmsCompound.set(key, compound.nmsCompound);
  }

  public NBTCompound createCompound(final String key) {
    final NBTCompound compound = new NBTCompound();
    nmsCompound.set(key, compound.nmsCompound);
    return compound;
  }

  @Nullable
  public NBTCompound getCompound(final String key) {
    if (!hasKey(key)) {
      return null;
    }
    final NBTCompound compound = new NBTCompound(nmsCompound.getCompound(key));
    return compound;
  }

  public Set<String> getKeys() {
    return nmsCompound.getKeys();
  }

  public boolean hasKey(final String key) {
    return nmsCompound.hasKey(key);
  }

  public String getString(final String key) {
    return nmsCompound.getString(key);
  }

  public int getInt(final String key) {
    return nmsCompound.getInt(key);
  }

  public short getShort(final String key) {
    return nmsCompound.getShort(key);
  }

  public byte getByte(final String key) {
    return nmsCompound.getByte(key);
  }

  public long getLong(final String key) {
    return nmsCompound.getLong(key);
  }

  public float getFloat(final String key) {
    return nmsCompound.getFloat(key);
  }

  public double getDouble(final String key) {
    return nmsCompound.getDouble(key);
  }

  public boolean getBoolean(final String key) {
    return nmsCompound.getBoolean(key);
  }

  public byte[] getByteArray(final String key) {
    return nmsCompound.getByteArray(key);
  }

  public int[] getIntArray(final String key) {
    return nmsCompound.getIntArray(key);
  }

  public void addStringList(final String key, final NBTStringList list) {
    nmsCompound.set(key, list.nmsList);
  }

  public NBTStringList createStringList(final String key) {
    final NBTStringList list = new NBTStringList();
    addStringList(key, list);
    return list;
  }

  public NBTStringList getStringList(final String key) {
    return new NBTStringList(nmsCompound.getList(key, 8));
  }

  @Override
  public NBTCompound clone() {
    return new NBTCompound(nmsCompound.clone());
  }

}
