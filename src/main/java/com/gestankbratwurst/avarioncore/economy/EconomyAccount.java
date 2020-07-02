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

  public EconomyAccount(JsonObject jsonObject) {
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

  public void remove(double amount) {
    this.balance -= amount;
  }

  public void removeBank(double amount) {
    this.bankBalance -= amount;
  }

  public void add(double amount) {
    this.balance += amount;
  }

  public void addBank(double amount) {
    this.bankBalance += amount;
  }

  public void set(double amount) {
    this.balance = amount;
  }

  public void setBank(double amount) {
    this.bankBalance = amount;
  }

  public JsonObject getAsJson() {
    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("Balance", balance);
    jsonObject.addProperty("BankBalance", balance);

    return jsonObject;
  }

}
