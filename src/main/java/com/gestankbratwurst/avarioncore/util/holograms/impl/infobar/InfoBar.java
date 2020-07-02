package com.gestankbratwurst.avarioncore.util.holograms.impl.infobar;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gestankbratwurst.avarioncore.util.holograms.impl.infobar.DoubleLinkedPacketHost.LinkedPacketType;
import com.gestankbratwurst.avarioncore.util.holograms.infobars.AbstractInfoBar;
import com.gestankbratwurst.avarioncore.util.holograms.infobars.InfoBarManager;
import com.gestankbratwurst.avarioncore.util.holograms.infobars.InfoLineSpacing;
import com.gestankbratwurst.avarioncore.util.packets.wrapper.WrapperPlayServerMount;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.server.v1_15_R1.ChatMessage;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.EntityPig;
import net.minecraft.server.v1_15_R1.EntityRabbit;
import net.minecraft.server.v1_15_R1.EntityTurtle;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class InfoBar extends AbstractInfoBar {

  public InfoBar(final Entity entity, final InfoBarManager infoBarManager) {
    super(entity, infoBarManager);
    lines = Lists.newArrayList();
    spawnPacketSupplier = Lists.newArrayList();
    lineEntityIDs = new IntOpenHashSet();
  }

  private final IntSet lineEntityIDs;
  private final ArrayList<Supplier<DoubleLinkedPacketHost>> spawnPacketSupplier;
  private final ArrayList<LineEntity> lines;

  @Override
  protected void showTo(final Player player) {
    viewingPlayer.add(player);
    final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
    for (int index = 0; index < spawnPacketSupplier.size(); index++) {
      final DoubleLinkedPacketHost packet = spawnPacketSupplier.get(index).get();
      if (packet.type == LinkedPacketType.NMS_PACKET) {
        packet.sendNMS(connection);
      } else {
        packet.sendProtocol(player);
      }
    }
  }

  @Override
  protected void hideFrom(final Player player) {
    viewingPlayer.remove(player);
    int index = 0;
    final int[] ids = new int[lineEntityIDs.size()];
    for (final int id : lineEntityIDs) {
      ids[index++] = id;
    }
    final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(ids);
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyPacket);
  }

  @Override
  public void editLine(final int index, final Function<String, String> lineEdit) {
    final LineEntity line = lines.get(index);
    final DoubleLinkedPacketHost packet = line.setNameAndGetMeta(lineEdit.apply(line.currentLine));
    for (final Player player : viewingPlayer) {
      packet.sendNMS(((CraftPlayer) player).getHandle().playerConnection);
    }
  }

  @Override
  public void setLine(final int index, final String newLine) {
    final LineEntity line = lines.get(index);
    final DoubleLinkedPacketHost packet = line.setNameAndGetMeta(newLine);
    for (final Player player : viewingPlayer) {
      packet.sendNMS(((CraftPlayer) player).getHandle().playerConnection);
    }
  }

  @Override
  public void addLine(final String newLine, final InfoLineSpacing spacing) {
    final LinePart spacingPart;

    switch (spacing) {
      case LARGE:
        spacingPart = new LargeSpacingEntity(entity.getLocation());
        break;
      case MEDIUM:
        spacingPart = new MediumSpacingEntity(entity.getLocation());
        break;
      case SMALL:
        spacingPart = new SmallSpacingEntity(entity.getLocation());
        break;
      default:
        spacingPart = new LargeSpacingEntity(entity.getLocation());
        break;
    }

    final LinePart lineEntity = new LineEntity(entity.getLocation(), newLine);

    final int lineID = lineEntity.getHandle().getId();
    final int spacingID = spacingPart.getHandle().getId();

    lineEntityIDs.add(lineID);
    lineEntityIDs.add(spacingID);

    infoBarManager.addMapping(lineID, getEntity());
    infoBarManager.addMapping(spacingID, getEntity());

    final ArrayList<Supplier<DoubleLinkedPacketHost>> newPackets = Lists.newArrayList();

    final net.minecraft.server.v1_15_R1.Entity hostEntity = lines.size() == 0 ?
        ((CraftEntity) entity).getHandle() : lines.get(lines.size() - 1);

    newPackets.add(() -> spacingPart.getLivingPacket());
    newPackets.add(() -> getMountPacket(hostEntity, spacingPart.getHandle()));
    newPackets.add(() -> spacingPart.getMetaPacket());

    newPackets.add(() -> lineEntity.getSpawnPacket());
    newPackets.add(() -> spacingPart.getMountPacket(lineEntity.getHandle().getId()));
    newPackets.add(() -> spacingPart.getMetaPacket());
    newPackets.add(() -> lineEntity.getMetaPacket());

    lines.add((LineEntity) lineEntity);

    final Map<Player, PlayerConnection> connections = Maps.newHashMap();

    for (final Player player : viewingPlayer) {
      connections.put(player, ((CraftPlayer) player).getHandle().playerConnection);
    }

    for (int i = 0; i < newPackets.size(); i++) {
      final Supplier<DoubleLinkedPacketHost> packetSupplier = newPackets.get(i);
      spawnPacketSupplier.add(packetSupplier);
      final DoubleLinkedPacketHost packet = packetSupplier.get();
      if (packet.type == LinkedPacketType.NMS_PACKET) {
        for (final PlayerConnection conn : connections.values()) {
          packet.sendNMS(conn);
        }
      } else {
        for (final Player player : connections.keySet()) {
          packet.sendProtocol(player);
        }
      }

    }
  }

  private DoubleLinkedPacketHost getMountPacket(final net.minecraft.server.v1_15_R1.Entity mount,
      final net.minecraft.server.v1_15_R1.Entity rider) {
    final WrapperPlayServerMount packet = new WrapperPlayServerMount();
    packet.setEntityID(mount.getId());
    packet.setPassengerIds(new int[]{rider.getId()});

    return DoubleLinkedPacketHost.of(packet.getHandle());
  }

  @Override
  public int getSize() {
    return lines.size();
  }

  static interface LinePart {

    public net.minecraft.server.v1_15_R1.Entity getHandle();

    public DoubleLinkedPacketHost getSpawnPacket();

    public DoubleLinkedPacketHost getMetaPacket();

    public DoubleLinkedPacketHost setNameAndGetMeta(String line);

    public DoubleLinkedPacketHost getMountPacket(int riderID);

    public DoubleLinkedPacketHost getLivingPacket();
  }

  private static final class LineEntity extends EntityArmorStand implements LinePart {

    public LineEntity(final Location location, final String line) {
      super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(),
          location.getZ());
      setMarker(true);
      setInvisible(true);
      setCustomNameVisible(line != null);
      if (getCustomNameVisible()) {
        currentLine = line;
        setCustomName(new ChatMessage(currentLine));
      } else {
        currentLine = "";
      }
    }

    private void setCurrentLine(final String line) {
      setCustomName(new ChatMessage(line));
      currentLine = line;
    }

    private String currentLine;

    @Override
    public DoubleLinkedPacketHost getSpawnPacket() {
      return DoubleLinkedPacketHost.of(new PacketPlayOutSpawnEntity(this));
    }

    @Override
    public DoubleLinkedPacketHost getMetaPacket() {
      return DoubleLinkedPacketHost
          .of(new PacketPlayOutEntityMetadata(getId(), datawatcher, true));
    }

    @Override
    public DoubleLinkedPacketHost setNameAndGetMeta(final String line) {
      setCurrentLine(line);
      return getMetaPacket();
    }

    @Override
    public DoubleLinkedPacketHost getMountPacket(final int riderID) {
      final WrapperPlayServerMount packet = new WrapperPlayServerMount();
      packet.setEntityID(getId());
      packet.setPassengerIds(new int[]{riderID});

      return DoubleLinkedPacketHost.of(packet.getHandle());
    }

    @Override
    public net.minecraft.server.v1_15_R1.Entity getHandle() {
      return this;
    }

    @Override
    public DoubleLinkedPacketHost getLivingPacket() {
      return null;
    }

  }

  private static final class SmallSpacingEntity extends EntityTurtle implements LinePart {

    public SmallSpacingEntity(final Location location) {
      super(EntityTypes.TURTLE, ((CraftWorld) location.getWorld()).getHandle());
      setInvisible(true);
      setPosition(location.getX(), location.getY(), location.getZ());
      setAge(-100);
      ageLocked = true;
    }


    @Override
    public DoubleLinkedPacketHost getSpawnPacket() {
      return DoubleLinkedPacketHost.of(new PacketPlayOutSpawnEntity(this));
    }

    @Override
    public DoubleLinkedPacketHost getMetaPacket() {
      return DoubleLinkedPacketHost
          .of(new PacketPlayOutEntityMetadata(getId(), datawatcher, true));
    }

    @Override
    public DoubleLinkedPacketHost setNameAndGetMeta(final String line) {
      setCustomName(new ChatMessage(line));
      return getMetaPacket();
    }

    @Override
    public DoubleLinkedPacketHost getMountPacket(final int riderID) {
      final WrapperPlayServerMount packet = new WrapperPlayServerMount();
      packet.setEntityID(getId());
      packet.setPassengerIds(new int[]{riderID});

      return DoubleLinkedPacketHost.of(packet.getHandle());
    }

    @Override
    public net.minecraft.server.v1_15_R1.Entity getHandle() {
      return this;
    }

    @Override
    public DoubleLinkedPacketHost getLivingPacket() {
      return DoubleLinkedPacketHost.of(new PacketPlayOutSpawnEntityLiving(this));
    }

  }

  private static final class MediumSpacingEntity extends EntityRabbit implements LinePart {

    public MediumSpacingEntity(final Location location) {
      super(EntityTypes.RABBIT, ((CraftWorld) location.getWorld()).getHandle());
      setPosition(location.getX(), location.getY(), location.getZ());
      setInvisible(true);
      setAge(-100);
      ageLocked = true;
    }


    @Override
    public DoubleLinkedPacketHost getSpawnPacket() {
      return DoubleLinkedPacketHost.of(new PacketPlayOutSpawnEntity(this));
    }

    @Override
    public DoubleLinkedPacketHost getMetaPacket() {
      return DoubleLinkedPacketHost
          .of(new PacketPlayOutEntityMetadata(getId(), datawatcher, true));
    }

    @Override
    public DoubleLinkedPacketHost setNameAndGetMeta(final String line) {
      setCustomName(new ChatMessage(line));
      return getMetaPacket();
    }

    @Override
    public DoubleLinkedPacketHost getMountPacket(final int riderID) {
      final WrapperPlayServerMount packet = new WrapperPlayServerMount();
      packet.setEntityID(getId());
      packet.setPassengerIds(new int[]{riderID});

      return DoubleLinkedPacketHost.of(packet.getHandle());
    }

    @Override
    public net.minecraft.server.v1_15_R1.Entity getHandle() {
      return this;
    }

    @Override
    public DoubleLinkedPacketHost getLivingPacket() {
      return DoubleLinkedPacketHost.of(new PacketPlayOutSpawnEntityLiving(this));
    }

  }

  private static final class LargeSpacingEntity extends EntityPig implements LinePart {

    public LargeSpacingEntity(final Location location) {
      super(EntityTypes.PIG, ((CraftWorld) location.getWorld()).getHandle());
      setPosition(location.getX(), location.getY(), location.getZ());
      setInvisible(true);
      setAge(-100);
      ageLocked = true;
    }


    @Override
    public DoubleLinkedPacketHost getSpawnPacket() {
      return DoubleLinkedPacketHost.of(new PacketPlayOutSpawnEntity(this));
    }

    @Override
    public DoubleLinkedPacketHost getMetaPacket() {
      return DoubleLinkedPacketHost
          .of(new PacketPlayOutEntityMetadata(getId(), datawatcher, true));
    }

    @Override
    public DoubleLinkedPacketHost setNameAndGetMeta(final String line) {
      setCustomName(new ChatMessage(line));
      return getMetaPacket();
    }

    @Override
    public DoubleLinkedPacketHost getMountPacket(final int riderID) {
      final WrapperPlayServerMount packet = new WrapperPlayServerMount();
      packet.setEntityID(getId());
      packet.setPassengerIds(new int[]{riderID});

      return DoubleLinkedPacketHost.of(packet.getHandle());
    }

    @Override
    public net.minecraft.server.v1_15_R1.Entity getHandle() {
      return this;
    }

    @Override
    public DoubleLinkedPacketHost getLivingPacket() {
      return DoubleLinkedPacketHost.of(new PacketPlayOutSpawnEntityLiving(this));
    }

  }

  @Override
  public boolean isInLineOfSight(final Player player) {
    return player.hasLineOfSight(entity);
  }

}