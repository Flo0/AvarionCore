package com.gestankbratwurst.avarioncore.protection;

import lombok.AllArgsConstructor;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 01.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@AllArgsConstructor
public enum RuleState {

  ALLOW("Erlaubt"),
  DENY("Verboten");

  private final String displayName;

}
