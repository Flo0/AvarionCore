package com.gestankbratwurst.avarioncore.economy;

import com.google.gson.JsonObject;
import lombok.Getter;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 30.06.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class EconomyAccount {

  public static String MONEY_SINGULAR = "$";
  public static String MONEY_PLURAL = "$";

  public EconomyAccount(final JsonObject jsonObject) {
    this.balance = jsonObject.get("Balance").getAsDouble();
    this.bankBalance = jsonObject.get("BankBalance").getAsDouble();
  }

  public EconomyAccount() {
    this.balance = 0D;
    this.bankBalance = 0D;
  }

  @Getter
  private double balance;
  @Getter
  private double bankBalance;

  public void remove(final double amount) {
    this.balance -= amount;
  }

  public void removeBank(final double amount) {
    this.bankBalance -= amount;
  }

  public void add(final double amount) {
    this.balance += amount;
  }

  public void addBank(final double amount) {
    this.bankBalance += amount;
  }

  public void set(final double amount) {
    this.balance = amount;
  }

  public void setBank(final double amount) {
    this.bankBalance = amount;
  }

  public JsonObject getAsJson() {
    final JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("Balance", this.balance);
    jsonObject.addProperty("BankBalance", this.bankBalance);

    return jsonObject;
  }

}
