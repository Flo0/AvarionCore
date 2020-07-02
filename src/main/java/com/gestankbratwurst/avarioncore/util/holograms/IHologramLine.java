package com.gestankbratwurst.avarioncore.util.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

public interface IHologramLine<T> {
    
	public abstract AbstractHologram getHostingHologram();
	public abstract T getCurrentValue();
	public abstract Location getLocation();
    public abstract void showTo(Player player);
    public abstract void hideFrom(Player player);
    public abstract void update(T newValue);
    public abstract HologramLineType getType();
    public abstract void registerClickAction(Consumer<Player> action);
    public abstract void onClick(Player player);
    public abstract void sendMove(Player player, Vector direction);
    
}