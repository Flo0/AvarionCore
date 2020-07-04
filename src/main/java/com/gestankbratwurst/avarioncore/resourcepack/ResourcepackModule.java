package com.gestankbratwurst.avarioncore.resourcepack;

import com.gestankbratwurst.avarioncore.AvarionCore;
import com.gestankbratwurst.avarioncore.resourcepack.distribution.ResourcepackListener;
import com.gestankbratwurst.avarioncore.resourcepack.distribution.ResourcepackManager;
import com.gestankbratwurst.avarioncore.resourcepack.packing.ResourcepackAssembler;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;

public class ResourcepackModule {

  private ResourcepackManager resourcepackManager;

  public String getModuleName() {
    return "ResourcepackModule";
  }

  public void enable(final AvarionCore plugin) {
    CompletableFuture.runAsync(() -> {
      try {
        System.out.println("§a -> PRE_ASSEMBLER");
        new ResourcepackAssembler(plugin).zipResourcepack();
        System.out.println("§a -> POST_ASSEMBLER");
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }).thenRun(() -> {
      System.out.println("§a -> MANAGER");
      this.resourcepackManager = new ResourcepackManager(plugin);
      Bukkit.getPluginManager().registerEvents(new ResourcepackListener(plugin, this.resourcepackManager), plugin);
    });
  }

  public void disable(final AvarionCore plugin) {
    if (this.resourcepackManager == null) {
      System.out.println("§c ResourcepackManager is null.");
    } else {
      this.resourcepackManager.shutdown();
    }
  }

}
