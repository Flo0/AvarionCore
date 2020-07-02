package com.gestankbratwurst.avarioncore.util.nbtapi;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.server.v1_15_R1.NBTBase;
import net.minecraft.server.v1_15_R1.NBTTagList;
import net.minecraft.server.v1_15_R1.NBTTagString;
import org.jetbrains.annotations.NotNull;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of CorePlugin and was created at the 28.11.2019
 *
 * CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class NBTStringList implements List<String> {

  public NBTStringList() {
    nmsList = new NBTTagList();
  }

  public NBTStringList(final NBTTagList nmsList) {
    this.nmsList = nmsList;
  }

  protected final NBTTagList nmsList;

  @Override
  public int size() {
    return nmsList.size();
  }

  @Override
  public boolean isEmpty() {
    return nmsList.isEmpty();
  }

  @Override
  public boolean contains(final Object o) {
    return nmsList.contains(o);
  }

  @NotNull
  @Override
  public Iterator<String> iterator() {
    final List<String> list = nmsList.stream().map(b -> b.asString()).collect(Collectors.toList());
    return list.iterator();
  }

  @Override
  public void forEach(final Consumer<? super String> action) {
    nmsList.forEach(b -> {
      action.accept(b.asString());
    });
  }

  @NotNull
  @Override
  public String[] toArray() {
    final int size = nmsList.size();
    final String[] array = new String[size];
    for (int index = 0; index < size; index++) {
      array[index] = nmsList.getString(index);
    }
    return array;
  }

  @NotNull
  @Override
  public <T> T[] toArray(@NotNull final T[] a) {
    return null;
  }

  @Override
  public boolean add(final String element) {
    return nmsList.add(NBTTagString.a(element));
  }

  @Override
  public boolean remove(final Object o) {
    return nmsList.remove(o);
  }

  @Override
  public boolean containsAll(@NotNull final Collection<?> c) {
    final List<String> list = nmsList.stream().map(NBTBase::asString).collect(Collectors.toList());
    return list.containsAll(c);
  }

  @Override
  public boolean addAll(@NotNull final Collection<? extends String> c) {
    for (final String element : c) {
      if (!add(element)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean addAll(final int index, @NotNull final Collection<? extends String> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(@NotNull final Collection<?> c) {
    c.forEach(this::remove);
    return true;
  }

  @Override
  public boolean retainAll(@NotNull final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    nmsList.clear();
  }

  @Override
  public String get(final int index) {
    return nmsList.getString(index);
  }

  @Override
  public String set(final int index, final String element) {
    return nmsList.set(index, NBTTagString.a(element)).asString();
  }

  @Override
  public void add(final int index, final String element) {
    nmsList.add(index, NBTTagString.a(element));
  }

  @Override
  public String remove(final int index) {
    return nmsList.remove(index).asString();
  }

  @Override
  public int indexOf(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int lastIndexOf(final Object o) {
    throw new UnsupportedOperationException();
  }

  @NotNull
  @Override
  public ListIterator<String> listIterator() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  @Override
  public ListIterator<String> listIterator(final int index) {
    throw new UnsupportedOperationException();
  }

  @NotNull
  @Override
  public List<String> subList(final int fromIndex, final int toIndex) {
    throw new UnsupportedOperationException();
  }

}
