package com.gestankbratwurst.avarioncore.util;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.util.actionbar.ActionBarManager;
import com.gestankbratwurst.avarioncore.util.common.BukkitTime;
import com.gestankbratwurst.avarioncore.util.common.NameSpaceFactory;
import com.gestankbratwurst.avarioncore.util.common.UtilBlock;
import com.gestankbratwurst.avarioncore.util.common.UtilChunk;
import com.gestankbratwurst.avarioncore.util.common.UtilItem;
import com.gestankbratwurst.avarioncore.util.common.UtilMobs;
import com.gestankbratwurst.avarioncore.util.common.UtilPlayer;
import com.gestankbratwurst.avarioncore.util.holograms.impl.HologramManager;
import com.gestankbratwurst.avarioncore.util.holograms.impl.infobar.InfoBar;
import com.gestankbratwurst.avarioncore.util.holograms.infobars.InfoBarManager;
import com.gestankbratwurst.avarioncore.util.items.display.ItemDisplayCompiler;
import com.gestankbratwurst.avarioncore.util.packets.adapter.ChunkTracker;
import com.gestankbratwurst.avarioncore.util.packets.adapter.EntityTracker;
import lombok.Getter;
import net.crytec.inventoryapi.anvil.AnvilAPI;
import net.crytec.libs.protocol.ProtocolAPI;
import net.crytec.libs.protocol.npc.NpcAPI;
import net.crytec.libs.protocol.skinclient.PlayerSkinManager;
import net.crytec.libs.protocol.tablist.TabListManager;

public class UtilModule {

  @Getter
  private HologramManager hologramManager;
  @Getter
  private ActionBarManager actionBarManager;
  @Getter
  private InfoBarManager infoBarManager;
  @Getter
  private ProtocolAPI protocolAPI;
  @Getter
  private NpcAPI npcAPI;
  @Getter
  private TabListManager tabListManager;
  @Getter
  private PlayerSkinManager playerSkinManager;

  public void enable(final AvarionCore plugin) {
    BukkitTime.start(plugin);
    NameSpaceFactory.init(plugin);
    ChunkTracker.init(plugin, plugin.getProtocolManager());
    EntityTracker.init(plugin, plugin.getProtocolManager());
    UtilChunk.init(plugin);
    UtilPlayer.init(plugin);
    UtilBlock.init(plugin);
    UtilMobs.init(plugin);
    UtilItem.init(plugin);
    new AnvilAPI(plugin);
    plugin.setDisplayCompiler(new ItemDisplayCompiler(plugin));
    plugin.getProtocolManager().addPacketListener(plugin.getDisplayCompiler());
    hologramManager = new HologramManager(plugin);
    playerSkinManager = new PlayerSkinManager();
    plugin.getCommandManager().registerCommand(new TestCommand(hologramManager));
    actionBarManager = new ActionBarManager(plugin);
    infoBarManager = new InfoBarManager(plugin, (entity) -> new InfoBar(entity, infoBarManager));
    protocolAPI = new ProtocolAPI(plugin);
    npcAPI = new NpcAPI(plugin);
//    tabListManager = new TabListManager(plugin, (p) -> et);
//    et = new EmptyTablist(tabListManager);
  }

}
