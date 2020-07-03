package com.gestankbratwurst.avarioncore.economy.abstraction;

import com.gestankbratwurst.avarioncore.data.AvarionPlayer;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 03.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public interface Sellable {

  double getCostToSell(AvarionPlayer avarionPlayer);

}