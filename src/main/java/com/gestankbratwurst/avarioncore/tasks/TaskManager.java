package com.gestankbratwurst.avarioncore.tasks;

import com.gestankbratwurst.avarioncore.AvarionCore;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {

  private static final int THREAD_POOL_SIZE = 2;

  public TaskManager(final AvarionCore plugin) {
    bukkitScheduler = Bukkit.getScheduler();
    scheduler = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
    this.plugin = plugin;
  }

  private final BukkitScheduler bukkitScheduler;
  private final ScheduledExecutorService scheduler;
  private final AvarionCore plugin;

  public <U> CompletableFuture asyncThenSync(U object, Function<? super U, ? super U> asyncAction, Consumer<U> afterAsync) {
    return CompletableFuture
        .completedFuture(object)
        .thenApplyAsync(asyncAction, scheduler)
        .thenRun(new BukkitSyncConsumer<>(object, afterAsync, plugin));
  }

  /**
   * Repeatedly executes a Runnable at a fixed rate. - Thread Async
   *
   * @param runnable the Runnable that is going to be executed.
   * @param delay    the delay before the first execution.
   * @param repeat   the delay between each repeated execution.
   * @param timeUnit the time units.
   * @return a ScheduledFuture
   */
  public ScheduledFuture<?> executeScheduledTask(final Runnable runnable, final long delay,
      final long repeat, final TimeUnit timeUnit) {
    return scheduler.scheduleAtFixedRate(runnable, delay, repeat, timeUnit);
  }

  public void incrementLines(ItemStack item, String identifier) {
    ItemMeta meta = item.getItemMeta();
    if (!meta.hasLore()) {
      return;
    }
    List<String> lore = meta.getLore();
    incrementLines(identifier, lore);
    meta.setLore(lore);
    item.setItemMeta(meta);
  }

  public void incrementLines(String identifier, List<String> lore) {
    // For every line in lore
    for (int index = 0; index < lore.size(); index++) {
      String line = lore.get(index);
      // Checks if line starts with identifier (Coal for example)
      if (stripColor(line).startsWith(identifier)) {
        // 1)Strip color 2)replace all but 0-9 3)parse to int
        int value = Integer.parseInt(extractNumber(stripColor(line)));
        // set a new lore line that replaces the old one
        lore.set(index, "§6" + identifier + ": §7" + ++value);
      }
    }
  }

  // Replaces everything but 0-9
  private String extractNumber(String input) {
    return input.replaceAll("[^0-9]", "");
  }

  // Strips color codes
  private String stripColor(String input) {
    return ChatColor.stripColor(input);
  }

  /**
   * Executes a Runnable Task Async once. - Thread Async
   *
   * @param runnable the runnable to execute.
   */
  public void executeTask(final Runnable runnable) {
    scheduler.execute(runnable);
  }

  /**
   * Executes a Runnable. - Bukkit Sync
   *
   * @param runnable the runnable to execute.
   * @return
   */
  public BukkitTask runBukkitSync(final Runnable runnable) {
    return bukkitScheduler.runTask(plugin, runnable);
  }

  public int runBukkitSyncDelayed(final Runnable runnable, final long delyTicks) {
    return bukkitScheduler.scheduleSyncDelayedTask(plugin, runnable, delyTicks);
  }

  /**
   * Executes a Runnable. - Bukkit Async
   *
   * @param runnable the runnable to execute.
   * @return
   */
  public BukkitTask runBukkitAsync(final Runnable runnable) {
    return bukkitScheduler.runTaskAsynchronously(plugin, runnable);
  }

  /**
   * Repeatedly executes a Runnable. - Bukkit Sync - Delays in Ticks
   *
   * @param runnable    the runnable to execute.
   * @param delayTicks  the delay before the first execution.
   * @param repeatTicks the dealy between each repeated execution.
   * @return a BukkitTask
   */
  public BukkitTask runRepeatedBukkit(final Runnable runnable, final long delayTicks,
      final long repeatTicks) {
    return bukkitScheduler.runTaskTimer(plugin, runnable, delayTicks, repeatTicks);
  }

  /**
   * Repeatedly executes a Runnable. - Bukkit Async - Delays in Ticks
   *
   * @param runnable    the runnable to execute.
   * @param delayTicks  the delay before the first execution.
   * @param repeatTicks the dealy between each repeated execution.
   * @return a BukkitTask
   */
  public BukkitTask runRepeatedBukkitAsync(final Runnable runnable, final long delayTicks,
      final long repeatTicks) {
    return bukkitScheduler
        .runTaskTimerAsynchronously(plugin, runnable, delayTicks, repeatTicks);
  }

  /**
   * Repeats a Runnable a fixed amount of times. - BukkitSync - Delays in Ticks
   *
   * @param runnable     the runnable to execute.
   * @param delayTicks   the delay before the first execution.
   * @param repeatDelay  the delay between each further execution.
   * @param repeatAmount the amount of repeats.
   */
  public void runFixedTimesBukkitSync(final Runnable runnable, final long delayTicks,
      final long repeatDelay, final int repeatAmount) {
    TickedRunnable.start(runnable, delayTicks, repeatDelay, repeatAmount, plugin, true);
  }

  /**
   * Repeats a Runnable a fixed amount of times. - BukkitSync - Delays in Ticks
   *
   * @param runnable     the runnable to execute.
   * @param delayTicks   the delay before the first execution.
   * @param repeatDelay  the delay between each further execution.
   * @param repeatAmount the amount of repeats.
   */
  public void runFixedTimesBukkitAsync(final Runnable runnable, final long delayTicks,
      final long repeatDelay, final int repeatAmount) {
    TickedRunnable.start(runnable, delayTicks, repeatDelay, repeatAmount, plugin, false);
  }

}
