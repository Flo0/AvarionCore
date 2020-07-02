package com.gestankbratwurst.avarioncore.resourcepack;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.resourcepack.distribution.ResourcepackListener;
import com.gestankbratwurst.avarioncore.resourcepack.distribution.ResourcepackManager;
import com.gestankbratwurst.avarioncore.resourcepack.packing.ResourcepackAssembler;
import com.gestankbratwurst.avarioncore.resourcepack.skins.Model;
import com.gestankbratwurst.avarioncore.resourcepack.skins.ModelItemCommand;
import com.gestankbratwurst.avarioncore.resourcepack.sounds.CustomSound;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.server.v1_15_R1.SoundCategory;
import org.bukkit.Bukkit;

public class ResourcepackModule {

  private ResourcepackManager resourcepackManager;

  public String getModuleName() {
    return "ResourcepackModule";
  }

  public void enable(final AvarionCore plugin) {
    CompletableFuture.runAsync(() -> {
      try {
        new ResourcepackAssembler(plugin).zipResourcepack();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).thenRun(() -> {
      resourcepackManager = new ResourcepackManager(plugin);
      Bukkit.getPluginManager().registerEvents(new ResourcepackListener(plugin, resourcepackManager), plugin);
    });

    plugin.getCommandManager().registerCommand(new ModelItemCommand());
    plugin.getCommandManager()
        .getCommandCompletions()
        .registerStaticCompletion("ModelItem",
            ImmutableList.copyOf(Arrays.stream(Model.values()).map(Enum::toString).collect(Collectors.toList())));
    plugin.getCommandManager()
        .getCommandCompletions()
        .registerStaticCompletion("CustomSound",
            ImmutableList.copyOf(Arrays.stream(CustomSound.values()).map(Enum::toString).collect(Collectors.toList())));
    plugin.getCommandManager()
        .getCommandCompletions()
        .registerStaticCompletion("SoundCategory",
            ImmutableList.copyOf(Arrays.stream(SoundCategory.values()).map(Enum::toString).collect(Collectors.toList())));
  }

  public void disable(final AvarionCore plugin) {
    resourcepackManager.shutdown();
  }

}
