package com.gestankbratwurst.avarioncore.protection;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
public enum ProtectionRule {

  DAMAGE_ANVIL("Anvil Schaden", RuleState.ALLOW, true), // Done
  SERVER_DESTROY_BLOCK("Block zerstören", RuleState.ALLOW, true), // Done
  TNT_PRIME("TNT entzünden", RuleState.ALLOW, true), // Done
  ENDERMAN_AGGRESSION("Enderman aggro", RuleState.ALLOW, true), // Done
  ENTITY_KNOCKBACK("Entity knockback", RuleState.ALLOW, true), // Done
  END_GATEWAY("Enderportal", RuleState.ALLOW, true), // Done
  PHANTOM_SPAWN("Phantome spawnen", RuleState.ALLOW, true), // Done
  THROW_EGG_HATCH("Geworfenes Ei schlüpfen", RuleState.ALLOW, true), // Done
  ENTITY_JUMP("Entity springt", RuleState.ALLOW, true), // Done
  TURTLE_LAY_EGG("Schildkröte legt Ei", RuleState.ALLOW, true), // Done
  ELYTRA_BOOST("Elytra boost", RuleState.ALLOW, false), // Done
  PLAYER_JUMP("Spieler springt", RuleState.ALLOW, false), // Done
  PLAYER_LAUNCH_PROJECTILE("Spieler schießt Projektil", RuleState.ALLOW, false), // Done
  PLAYER_PICKUP_EXP("Spieler sammelt EXP", RuleState.ALLOW, false), // Done
  BREAK_BLOCK("Spieler baut Block ab", RuleState.ALLOW, false), // Done
  BLOCK_BURN("Block verbrennt", RuleState.ALLOW, true), // Done
  BLOCK_CHECK_BUILD("Block setzen Permission check", RuleState.ALLOW, false), // Done
  BLOCK_DAMAGE("Spieler schadet Block", RuleState.ALLOW, false), // Done
  BLOCK_DISPENSE("Block dispenced Item", RuleState.ALLOW, true), // Done
  BLOCK_DROP_ITEM("Block droppt Item", RuleState.ALLOW, false), // Done
  BLOCK_EXPLODE("Block explodiert", RuleState.ALLOW, true), // Done
  BLOCK_FADES("Block schwindet dahin", RuleState.ALLOW, true), // Done
  BLOCK_FERTILIZE("Block wird gedüngt", RuleState.ALLOW, false), // Done
  BLOCK_FORM("Block wird geformt", RuleState.ALLOW, true), // Done
  BLOCK_GROW("Block wächst", RuleState.ALLOW, true), // Done
  BLOCK_IGNITE("Block entzündet", RuleState.ALLOW, true), // Done
  BLOCK_PISTON("Pistons", RuleState.ALLOW, true), // Done
  BLOCK_PLACE("Block platzieren", RuleState.ALLOW, false), // Done
  BLOCK_SPREAD("Block verbreitet sich", RuleState.ALLOW, true), // Done
  ENTITY_FORMS_BLOCK("Entity formt block", RuleState.ALLOW, true), // Done
  SIGN_CHANGE("Schild ändern", RuleState.ALLOW, false), // Done
  CREATURE_SPAWN("Kreatur spawnt", RuleState.ALLOW, true), // Done
  AIR_CHANGE("Atemluft ändern", RuleState.ALLOW, true), // Done
  ENTITY_BREAK_DOOR("Entity zerbricht Tür", RuleState.ALLOW, true), // Done
  ENTITY_BREED("Entity vermehrt sich", RuleState.ALLOW, true), // Done
  ENTITY_CHANGE_BLOCK("Entity ändert Block", RuleState.ALLOW, true), // Done
  ENTITY_DAMAGE_BY_BLOCK("Entity wird von Block geschadet", RuleState.ALLOW, true), // Done
  ENTITY_DAMAGE_BY_ENTITY("Entity wird von Entity geschadet", RuleState.ALLOW, true), // Done
  ENTITY_DAMAGE_GENERAL("Entity bekommt Schaden", RuleState.ALLOW, true), // Done
  ENTITY_DROP_ITEM("Entity droppt Item", RuleState.ALLOW, true), // Done
  ENTITY_EXPLODE("Entity explodiert", RuleState.ALLOW, true), // Done
  ENTITY_INTERACT("Entity interagiert", RuleState.ALLOW, true), // Done
  ENTITY_PICKUP_ITEM("Entity nimmt Item auf", RuleState.ALLOW, true), // Done
  ENTITY_POTION_EFFECT("Entity trankeffekt", RuleState.ALLOW, true), // Done
  ENTITY_REGAIN_HEALTH("Entity heilt sich", RuleState.ALLOW, true), // Done
  ENTITY_RESURRECT("Entity wiederbelebt", RuleState.ALLOW, true), // Done
  ENTITY_SHOOT_BOW("Entity schießt mit Bogen", RuleState.ALLOW, true), // Done
  ENTITY_TAME("Entity zähmen", RuleState.ALLOW, true), // Done
  ENTITY_GLIDE("Entity glidet", RuleState.ALLOW, true), // Done
  ENTITY_SWIM("Entity schwimmt", RuleState.ALLOW, true), // Done
  FOOD_LEVEL_CHANGE("Essenslevel ändert sich", RuleState.ALLOW, false), // Done
  ITEM_DESPAWN("Item despawnt", RuleState.ALLOW, true), // Done
  PROJECTILE_LAUNCH("Projektil wird geschossen", RuleState.ALLOW, true), // Done
  PLAYER_CHAT("Player chat", RuleState.ALLOW, false), // Done
  PLAYER_ARMORSTAND("Player ändert Armorstand", RuleState.ALLOW, false), // Done
  PLAYER_PICKUP_ITEM("Spieler nimmt Item auf", RuleState.ALLOW, false), // Done
  PLAYER_BUCKET("Spieler benutzt Eimer", RuleState.ALLOW, false), // Done
  PLAYER_DROP_ITEM("Spieler droppt Item", RuleState.ALLOW, false), // Done
  PLAYER_FISH("Spieler fischt", RuleState.ALLOW, false), // Done
  PLAYER_INTERACT_ENTITY("Spieler interagiert mit Entity", RuleState.ALLOW, false), // Done
  PLAYER_INTERACT("Spieler interagiert", RuleState.ALLOW, false), // Done
  PLAYER_PORTAL("Spieler benutzt Portal", RuleState.ALLOW, false), // Done
  PLAYER_TELEPORT("Spieler teleportiert", RuleState.ALLOW, false); // Done

  @Getter
  private final String displayName;
  @Getter
  private final RuleState defaultState;
  @Getter
  private final boolean globalContext;

}
