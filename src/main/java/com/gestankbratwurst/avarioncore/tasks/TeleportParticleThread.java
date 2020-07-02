package com.gestankbratwurst.avarioncore.tasks;

import com.gestankbratwurst.avarioncore.AvarionCore;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class TeleportParticleThread implements Runnable {

  public TeleportParticleThread(Entity entity, int ticks, double radius) {
    AvarionCore plugin = AvarionCore.getPlugin(AvarionCore.class);
    this.radius = radius;
    amount = this.radius * 20.0;
    inc = 6.283185307179586 / this.amount;
    this.interval = ticks / 25;
    this.sizePerIncl = 25F / ticks;
    this.entity = entity;
    this.world = entity.getWorld();
    task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 1, 1);
  }

  private final double radius;
  private final double amount;
  private final double inc;
  private final float sizePerIncl;
  private final BukkitTask task;
  private final Entity entity;


  private double x;
  private double z;
  private double y;
  private double angle;
  private final World world;

  private int interval = 10;
  private int ticksRun = 0;
  private float size = 0.2F;


  public void cancel() {
    task.cancel();
  }

  @Override
  public void run() {
    if (ticksRun % 10 == 0 && interval > 1) {
      interval -= 1;
      size += sizePerIncl;
    }

    if (interval < 1 || ticksRun % interval == 0) {
      Location location = entity.getLocation();
      this.angle = (double) entity.getTicksLived() * this.inc;
      this.x = this.radius * Math.cos(this.angle);
      this.z = this.radius * Math.sin(this.angle);
      this.y = this.radius * Math.cos(this.angle);
      Vector vector = new Vector(this.x, this.y + 1.0, this.z);
      location.add(vector);
      world.spawnParticle(Particle.REDSTONE, location, 1, 0.0, 0.0, 0.0, 0.0, new DustOptions(Color.PURPLE, size));
      location.subtract(vector);
      vector.setY(-vector.getY() + 2.0);
      location.add(vector);
      world.spawnParticle(Particle.REDSTONE, location, 1, 0.0, 0.0, 0.0, 0.0, new DustOptions(Color.PURPLE, size));
      if (ticksRun % 10 * interval == 0) {
        world.playSound(location, Sound.BLOCK_PORTAL_AMBIENT, 0.2F, 1.0F);
      }
    }
    ticksRun++;
  }
}