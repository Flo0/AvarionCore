package com.gestankbratwurst.avarioncore;

import co.aikar.commands.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.gestankbratwurst.avarioncore.data.AvarionDataManager;
import com.gestankbratwurst.avarioncore.data.AvarionIO;
import com.gestankbratwurst.avarioncore.data.impl.MongoIO;
import com.gestankbratwurst.avarioncore.economy.EconomyCommand;
import com.gestankbratwurst.avarioncore.economy.MoneyItemHandler;
import com.gestankbratwurst.avarioncore.friends.FriendCommand;
import com.gestankbratwurst.avarioncore.protection.ProtectionCommand;
import com.gestankbratwurst.avarioncore.protection.ProtectionManager;
import com.gestankbratwurst.avarioncore.protection.ProtectionRule;
import com.gestankbratwurst.avarioncore.protection.RuleState;
import com.gestankbratwurst.avarioncore.resourcepack.ResourcepackModule;
import com.gestankbratwurst.avarioncore.tasks.TaskManager;
import com.gestankbratwurst.avarioncore.util.Msg;
import com.gestankbratwurst.avarioncore.util.UtilModule;
import com.gestankbratwurst.avarioncore.util.common.UtilBlock;
import com.gestankbratwurst.avarioncore.util.items.display.ItemDisplayCompiler;
import com.gestankbratwurst.avarioncore.web.WebManager;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.crytec.inventoryapi.InventoryAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class AvarionCore extends JavaPlugin {

  @Getter
  private static AvarionCore instance;

  @Getter
  private ProtocolManager protocolManager;
  @Getter
  private TaskManager taskManager;
  @Getter
  @Setter
  private ItemDisplayCompiler displayCompiler;
  @Getter
  private PaperCommandManager commandManager;
  @Getter
  private UtilModule utilModule;
  @Getter
  private ResourcepackModule resourcepackModule;
  @Getter
  private AvarionIO avarionIO;
  @Getter
  private AvarionDataManager avarionDataManager;
  @Getter
  private ProtectionManager protectionManager;
  @Getter
  private MoneyItemHandler moneyItemHandler;
  @Getter
  private WebManager webManager;

  @Override
  public void onEnable() {

    instance = this;
    new InventoryAPI(this);
    Msg.init(this);

    this.avarionIO = new MongoIO();
    this.commandManager = new PaperCommandManager(this);
    this.taskManager = new TaskManager(this);
    this.protocolManager = ProtocolLibrary.getProtocolManager();

    this.utilModule = new UtilModule();
    this.utilModule.enable(this);

    this.resourcepackModule = new ResourcepackModule();
    this.resourcepackModule.enable(this);

    this.avarionDataManager = new AvarionDataManager(this);
    this.protectionManager = new ProtectionManager(this);

    this.moneyItemHandler = new MoneyItemHandler(this);
    
    this.webManager = new WebManager(this);

    this.registerCommands();
  }

  private void registerCommands() {
    this.commandManager.getCommandCompletions().registerStaticCompletion("ProtectionRule", Arrays
        .stream(ProtectionRule.values())
        .map(Enum::toString)
        .collect(Collectors.toList()));

    this.commandManager.getCommandCompletions().registerStaticCompletion("RuleState", Arrays
        .stream(RuleState.values())
        .map(Enum::toString)
        .collect(Collectors.toList()));

    this.commandManager.registerCommand(new ProtectionCommand(this.protectionManager));
    this.commandManager.registerCommand(new FriendCommand(this));
    this.commandManager.registerCommand(new EconomyCommand(this.moneyItemHandler));
  }

  @Override
  public void onDisable() {
    this.resourcepackModule.disable(this);
    this.protectionManager.flushData();
    this.avarionDataManager.flushData();
    UtilBlock.terminate(this);
  }

}