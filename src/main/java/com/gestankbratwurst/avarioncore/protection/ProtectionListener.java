package com.gestankbratwurst.avarioncore.protection;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.entity.EndermanAttackPlayerEvent;
import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.entity.EntityTeleportEndGatewayEvent;
import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import com.destroystokyo.paper.event.entity.TurtleLayEggEvent;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.data.AvarionDataManager;
import com.gestankbratwurst.avarioncore.data.AvarionPlayer;
import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.gestankbratwurst.avarioncore.tasks.TaskManager;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.UUID;
import net.crytec.libs.protocol.holograms.AbstractHologram;
import net.crytec.libs.protocol.holograms.impl.HologramManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 02.07.2020
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ProtectionListener implements Listener {

  public ProtectionListener(AvarionCore plugin, ProtectionManager protectionManager) {
    this.avarionDataManager = plugin.getAvarionDataManager();
    this.protectionManager = protectionManager;
    this.lastXTime = new Object2LongOpenHashMap<>();
    this.hologramManager = plugin.getUtilModule().getHologramManager();
    this.taskManager = plugin.getTaskManager();
  }

  private final TaskManager taskManager;
  private final HologramManager hologramManager;
  private final ProtectionManager protectionManager;
  private final AvarionDataManager avarionDataManager;
  private final Object2LongMap<UUID> lastXTime;

  private void showX(AvarionPlayer avarionPlayer, String message) {
    long last = lastXTime.getLong(avarionPlayer.getPlayerID());
    if (System.currentTimeMillis() - last > 500) {
      lastXTime.put(avarionPlayer.getPlayerID(), System.currentTimeMillis());
      Player player = avarionPlayer.getPlayer();
      if (player == null) {
        throw new IllegalStateException();
      }
      Location eyes = player.getEyeLocation();
      eyes.add(eyes.getDirection().multiply(1.5));
      AbstractHologram hologram = hologramManager.createHologram(eyes);
      hologram.appendTextLine("" + Model.RED_X.getChar());
      taskManager.runBukkitSyncDelayed(hologram::delete, 20L);
      avarionPlayer.sendMessage("Protection", message);
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    lastXTime.removeLong(event.getPlayer().getUniqueId());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(AnvilDamagedEvent event) {
    Location loc = event.getInventory().getLocation();
    if (loc == null) {
      return;
    }
    if (protectionManager.getEnvironmentRules(loc.getBlock()).getState(ProtectionRule.DAMAGE_ANVIL) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockDestroyEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.SERVER_DESTROY_BLOCK) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(TNTPrimeEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.TNT_PRIME) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EndermanAttackPlayerEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENDERMAN_AGGRESSION)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityKnockbackByEntityEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_KNOCKBACK)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityTeleportEndGatewayEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.END_GATEWAY)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PhantomPreSpawnEvent event) {
    if (protectionManager.getEnvironmentRules(event.getSpawnLocation().getBlock()).getState(ProtectionRule.PHANTOM_SPAWN)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityJumpEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_JUMP)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(TurtleLayEggEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_JUMP)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerElytraBoostEvent event) {
    Block block = event.getPlayer().getLocation().getBlock();
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(block, avarionPlayer).getState(ProtectionRule.ELYTRA_BOOST) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier nicht boosten.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerJumpEvent event) {
    Block block = event.getPlayer().getLocation().getBlock();
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(block, avarionPlayer).getState(ProtectionRule.PLAYER_JUMP) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier nicht springen.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerLaunchProjectileEvent event) {
    Block block = event.getPlayer().getLocation().getBlock();
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(block, avarionPlayer).getState(ProtectionRule.PLAYER_LAUNCH_PROJECTILE) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Projektile shießen.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerPickupExperienceEvent event) {
    Block block = event.getPlayer().getLocation().getBlock();
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(block, avarionPlayer).getState(ProtectionRule.PLAYER_PICKUP_EXP) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Exp aufheben.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockBreakEvent event) {
    Block block = event.getBlock();
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(block, avarionPlayer).getState(ProtectionRule.BREAK_BLOCK) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Blöcke abbauen.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockBurnEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.BLOCK_BURN) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockCanBuildEvent event) {
    Block block = event.getBlock();
    if (event.getPlayer() == null) {
      return;
    }
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(block, avarionPlayer).getState(ProtectionRule.BLOCK_CHECK_BUILD) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier nicht bauen.");
      event.setBuildable(false);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockDamageEvent event) {
    Block block = event.getBlock();
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(block, avarionPlayer).getState(ProtectionRule.BLOCK_DAMAGE) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Blöcke beschädigen.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockDispenseEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.BLOCK_DISPENSE) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockDropItemEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getBlock(), avarionPlayer).getState(ProtectionRule.BLOCK_DROP_ITEM) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Items fallen lassen.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockExplodeEvent event) {
    event.blockList()
        .removeIf(block -> protectionManager.getEnvironmentRules(block).getState(ProtectionRule.BLOCK_EXPLODE) == RuleState.DENY);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockFadeEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.BLOCK_FADES) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockFertilizeEvent event) {
    if (event.getPlayer() == null) {
      return;
    }
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getBlock(), avarionPlayer).getState(ProtectionRule.BLOCK_FERTILIZE) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier nicht düngen.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockFormEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.BLOCK_FORM) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockGrowEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.BLOCK_GROW) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockIgniteEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.BLOCK_IGNITE) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockPistonExtendEvent event) {
    for (Block block : event.getBlocks()) {
      if (protectionManager.getEnvironmentRules(block).getState(ProtectionRule.BLOCK_PISTON) == RuleState.DENY) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockPistonRetractEvent event) {
    for (Block block : event.getBlocks()) {
      if (protectionManager.getEnvironmentRules(block).getState(ProtectionRule.BLOCK_PISTON) == RuleState.DENY) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockPlaceEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getBlock(), avarionPlayer).getState(ProtectionRule.BLOCK_PLACE) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Blöcke platzieren.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(BlockSpreadEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.BLOCK_SPREAD) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityBlockFormEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.ENTITY_FORMS_BLOCK) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(SignChangeEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getBlock(), avarionPlayer).getState(ProtectionRule.SIGN_CHANGE) == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Schilder verändern.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(CreatureSpawnEvent event) {
    if (protectionManager.getEnvironmentRules(event.getLocation().getBlock()).getState(ProtectionRule.CREATURE_SPAWN)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PreCreatureSpawnEvent event) {
    if (protectionManager.getEnvironmentRules(event.getSpawnLocation().getBlock()).getState(ProtectionRule.CREATURE_SPAWN)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PreSpawnerSpawnEvent event) {
    if (protectionManager.getEnvironmentRules(event.getSpawnLocation().getBlock()).getState(ProtectionRule.CREATURE_SPAWN)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(SpawnerSpawnEvent event) {
    if (protectionManager.getEnvironmentRules(event.getLocation().getBlock()).getState(ProtectionRule.CREATURE_SPAWN) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityAirChangeEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.AIR_CHANGE)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityBreakDoorEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_BREAK_DOOR)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityBreedEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_BREED)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityChangeBlockEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.ENTITY_CHANGE_BLOCK)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityDamageByBlockEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_DAMAGE_BY_BLOCK)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityDamageByEntityEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_DAMAGE_BY_ENTITY)
        == RuleState.DENY
        ||
        protectionManager.getEnvironmentRules(event.getDamager().getLocation().getBlock()).getState(ProtectionRule.ENTITY_DAMAGE_BY_ENTITY)
            == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityDamageEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_DAMAGE_GENERAL)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityDropItemEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_DROP_ITEM)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityExplodeEvent event) {
    event.blockList()
        .removeIf(block -> protectionManager.getEnvironmentRules(block).getState(ProtectionRule.ENTITY_EXPLODE) == RuleState.DENY);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityInteractEvent event) {
    if (protectionManager.getEnvironmentRules(event.getBlock()).getState(ProtectionRule.ENTITY_INTERACT) == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityPickupItemEvent event) {
    if (protectionManager.getEnvironmentRules(event.getItem().getLocation().getBlock()).getState(ProtectionRule.ENTITY_PICKUP_ITEM)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityPotionEffectEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_POTION_EFFECT)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityRegainHealthEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_REGAIN_HEALTH)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityResurrectEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_RESURRECT)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityShootBowEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_SHOOT_BOW)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityTameEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_TAME)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityToggleGlideEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_GLIDE)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
    event.getEntity().getWorld().getChunkAtAsync(event.getEntity().getLocation());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(EntityToggleSwimEvent event) {
    if (protectionManager.getEnvironmentRules(event.getEntity().getLocation().getBlock()).getState(ProtectionRule.ENTITY_SWIM)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getEntity().getUniqueId());
    if (protectionManager.getPlayerRules(event.getEntity().getLocation().getBlock(), avarionPlayer).getState(ProtectionRule.SIGN_CHANGE)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(ItemDespawnEvent event) {
    if (protectionManager.getEnvironmentRules(event.getLocation().getBlock()).getState(ProtectionRule.ITEM_DESPAWN)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(ProjectileLaunchEvent event) {
    if (protectionManager.getEnvironmentRules(event.getLocation().getBlock()).getState(ProtectionRule.ITEM_DESPAWN)
        == RuleState.DENY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(AsyncPlayerChatEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getPlayer().getLocation().getBlock(), avarionPlayer).getState(ProtectionRule.PLAYER_CHAT)
        == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier nichts schreiben.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerArmorStandManipulateEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getRightClicked().getLocation().getBlock(), avarionPlayer)
        .getState(ProtectionRule.PLAYER_ARMORSTAND)
        == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Armorstands verändern.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerAttemptPickupItemEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getItem().getLocation().getBlock(), avarionPlayer)
        .getState(ProtectionRule.PLAYER_PICKUP_ITEM)
        == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Items aufheben.");
      event.setCancelled(true);
    }
  }

  // TODO use all sub events
//  @EventHandler(priority = EventPriority.LOWEST)
//  public void onEvent(PlayerBucketEvent event) {
//    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
//    if (protectionManager.getPlayerRules(event.getBlock(), avarionPlayer)
//        .getState(ProtectionRule.PLAYER_BUCKET)
//        == RuleState.DENY) {
//      event.setCancelled(true);
//    }
//  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerDropItemEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getPlayer().getLocation().getBlock(), avarionPlayer)
        .getState(ProtectionRule.PLAYER_DROP_ITEM)
        == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Items droppen.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerFishEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getHook().getLocation().getBlock(), avarionPlayer)
        .getState(ProtectionRule.PLAYER_FISH)
        == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier nicht fischen.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerInteractAtEntityEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getRightClicked().getLocation().getBlock(), avarionPlayer)
        .getState(ProtectionRule.PLAYER_INTERACT_ENTITY)
        == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier nicht mit Entities interagieren.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerInteractEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (event.getClickedBlock() == null) {
      return;
    }
    if (protectionManager.getPlayerRules(event.getClickedBlock(), avarionPlayer).getState(ProtectionRule.PLAYER_INTERACT)
        == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier nicht interagieren.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerPortalEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getFrom().getBlock(), avarionPlayer).getState(ProtectionRule.PLAYER_PORTAL)
        == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst hier keine Portale benutzen.");
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEvent(PlayerTeleportEvent event) {
    AvarionPlayer avarionPlayer = avarionDataManager.getOnlineData(event.getPlayer().getUniqueId());
    if (protectionManager.getPlayerRules(event.getFrom().getBlock(), avarionPlayer).getState(ProtectionRule.PLAYER_PORTAL)
        == RuleState.DENY) {
      showX(avarionPlayer, "Du kannst dich hier nicht fort teleportieren.");
      event.setCancelled(true);
    }
  }

}