package com.gestankbratwurst.avarioncore.util;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.util.actionbar.ActionBarManager;
import com.gestankbratwurst.avarioncore.util.common.BukkitTime;
import com.gestankbratwurst.avarioncore.util.common.NameSpaceFactory;
import com.gestankbratwurst.avarioncore.util.common.UtilBlock;
import com.gestankbratwurst.avarioncore.util.common.UtilItem;
import com.gestankbratwurst.avarioncore.util.common.UtilMobs;
import com.gestankbratwurst.avarioncore.util.common.UtilPlayer;
import com.gestankbratwurst.avarioncore.util.items.display.ItemDisplayCompiler;
import lombok.Getter;
import net.crytec.inventoryapi.anvil.AnvilAPI;
import net.crytec.libs.protocol.ProtocolAPI;
import net.crytec.libs.protocol.holograms.impl.HologramManager;
import net.crytec.libs.protocol.holograms.impl.infobar.InfoBar;
import net.crytec.libs.protocol.holograms.infobars.InfoBarManager;
import net.crytec.libs.protocol.npc.NpcAPI;
import net.crytec.libs.protocol.skinclient.PlayerSkinManager;
import net.crytec.libs.protocol.tablist.TabListManager;
import net.crytec.libs.protocol.tablist.implementation.EmptyTablist;

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
    UtilPlayer.init(plugin);
    UtilBlock.init(plugin);
    UtilMobs.init(plugin);
    UtilItem.init(plugin);
    new AnvilAPI(plugin);
    plugin.setDisplayCompiler(new ItemDisplayCompiler(plugin));
    plugin.getProtocolManager().addPacketListener(plugin.getDisplayCompiler());
    this.hologramManager = new HologramManager(plugin);
    this.playerSkinManager = new PlayerSkinManager();
    this.actionBarManager = new ActionBarManager(plugin);
    this.infoBarManager = new InfoBarManager(plugin, (entity) -> new InfoBar(entity, this.infoBarManager));
    this.protocolAPI = new ProtocolAPI(plugin);
    this.npcAPI = new NpcAPI(plugin);
    final EmptyTablist et = new EmptyTablist(this.tabListManager);
    this.tabListManager = new TabListManager(plugin, (p) -> et);
  }

}
