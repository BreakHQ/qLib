/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.WrappedDataWatcher
 *  com.comphenix.protocol.wrappers.WrappedWatchableObject
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterators
 *  javafx.util.Pair
 *  net.minecraft.server.v1_7_R4.Packet
 *  net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.hologram;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.util.Pair;
import net.frozenorb.qlib.hologram.Hologram;
import net.frozenorb.qlib.hologram.HologramBuilder;
import net.frozenorb.qlib.hologram.HologramLine;
import net.frozenorb.qlib.hologram.HologramRegistry;
import net.frozenorb.qlib.hologram.packets.HologramPacket;
import net.frozenorb.qlib.hologram.packets.HologramPacketProvider;
import net.frozenorb.qlib.hologram.packets.v1_7.Minecraft17HologramPacketProvider;
import net.frozenorb.qlib.hologram.packets.v1_8.Minecraft18HologramPacketProvider;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.tab.TabUtils;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BaseHologram
implements Hologram {
    private Collection<UUID> viewers;
    protected Location location;
    protected List<HologramLine> lastLines = new ArrayList<HologramLine>();
    protected List<HologramLine> lines = new ArrayList<HologramLine>();
    protected final Set<UUID> currentWatchers = new HashSet<UUID>();
    protected static final double distance = 0.23;

    protected BaseHologram(HologramBuilder builder) {
        if (builder.getLocation() == null) {
            throw new IllegalArgumentException("Please provide a location for the hologram using HologramBuilder#at(Location)");
        }
        this.viewers = builder.getViewers();
        this.location = builder.getLocation();
        for (String line : builder.getLines()) {
            this.lines.add(new HologramLine(line));
        }
    }

    @Override
    public void send() {
        Collection viewers = this.viewers;
        if (viewers == null) {
            viewers = ImmutableList.copyOf((Collection)qLib.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer((UUID)uuid);
            if (player == null || !player.isOnline()) continue;
            this.show(player);
        }
        HologramRegistry.getHolograms().add(this);
    }

    @Override
    public void destroy() {
        Collection viewers = this.viewers;
        if (viewers == null) {
            viewers = ImmutableList.copyOf((Collection)qLib.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer((UUID)uuid);
            if (player == null || !player.isOnline()) continue;
            this.destroy0(player);
        }
        if (this.viewers != null) {
            this.viewers.clear();
        }
        HologramRegistry.getHolograms().remove(this);
    }

    @Override
    public void addLines(String ... lines) {
        for (String line : lines) {
            this.lines.add(new HologramLine(line));
        }
        this.update();
    }

    @Override
    public void setLine(int index, String line) {
        if (index > this.lines.size() - 1) {
            this.lines.add(new HologramLine(line));
        } else if (this.lines.get(index) != null) {
            this.lines.get(index).setText(line);
        } else {
            this.lines.set(index, new HologramLine(line));
        }
        this.update();
    }

    @Override
    public void setLines(Collection<String> lines) {
        Collection viewers = this.viewers;
        if (viewers == null) {
            viewers = ImmutableList.copyOf((Collection)qLib.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer((UUID)uuid);
            if (player == null || !player.isOnline()) continue;
            this.destroy0(player);
        }
        this.lines.clear();
        for (String line : lines) {
            this.lines.add(new HologramLine(line));
        }
        this.update();
    }

    @Override
    public List<String> getLines() {
        ArrayList<String> lines = new ArrayList<String>();
        for (HologramLine line : this.lines) {
            lines.add(line.getText());
        }
        return lines;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    protected List<HologramLine> rawLines() {
        return this.lines;
    }

    protected void show(Player player) {
        if (!player.getLocation().getWorld().equals((Object)this.location.getWorld())) {
            return;
        }
        Location first = this.location.clone().add(0.0, (double)this.lines.size() * 0.23, 0.0);
        for (HologramLine line : this.lines) {
            this.showLine(player, first.clone(), line);
            first.subtract(0.0, 0.23, 0.0);
        }
        this.currentWatchers.add(player.getUniqueId());
    }

    protected Pair<Integer, Integer> showLine(Player player, Location loc, HologramLine line) {
        HologramPacketProvider packetProvider = this.getPacketProviderForPlayer(player);
        HologramPacket hologramPacket = packetProvider.getPacketsFor(loc, line);
        if (hologramPacket != null) {
            hologramPacket.sendToPlayer(player);
            return new Pair((Object)hologramPacket.getEntityIds().get(0), (Object)hologramPacket.getEntityIds().get(1));
        }
        return null;
    }

    protected void destroy0(Player player) {
        ArrayList<Integer> ints = new ArrayList<Integer>();
        for (HologramLine line : this.lines) {
            if (line.getHorseId() == -1337) {
                ints.add(line.getSkullId());
                continue;
            }
            ints.add(line.getSkullId());
            ints.add(line.getHorseId());
        }
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.convertIntegers(ints));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)packet);
        this.currentWatchers.remove(player.getUniqueId());
    }

    protected int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = integers.get(i);
        }
        return ret;
    }

    public void update() {
        Collection viewers = this.getViewers();
        if (viewers == null) {
            viewers = ImmutableList.copyOf((Collection)qLib.getInstance().getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
        }
        for (UUID uuid : viewers) {
            Player player = Bukkit.getPlayer((UUID)uuid);
            if (player == null || !player.isOnline()) continue;
            this.update(player);
        }
        this.lastLines.addAll(this.lines);
    }

    public void update(Player player) {
        if (!player.getLocation().getWorld().equals((Object)this.location.getWorld())) {
            return;
        }
        if (this.lastLines.size() != this.lines.size()) {
            this.destroy0(player);
            this.show(player);
            return;
        }
        for (int index = 0; index < this.rawLines().size(); ++index) {
            HologramLine line = this.rawLines().get(index);
            String text = ChatColor.translateAlternateColorCodes((char)'&', (String)line.getText());
            boolean is18 = TabUtils.is18(player);
            try {
                PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
                container.getIntegers().write(0, (Object)(is18 ? line.getSkullId() : line.getHorseId()));
                WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
                if (is18) {
                    wrappedDataWatcher.setObject(2, (Object)text);
                } else {
                    wrappedDataWatcher.setObject(10, (Object)text);
                }
                List<Object> watchableObjects = Arrays.asList(Iterators.toArray((Iterator)wrappedDataWatcher.iterator(), WrappedWatchableObject.class));
                container.getWatchableCollectionModifier().write(0, watchableObjects);
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
                }
                catch (Exception exception) {}
                continue;
            }
            catch (IndexOutOfBoundsException e) {
                this.destroy0(player);
                this.show(player);
            }
        }
    }

    private HologramPacketProvider getPacketProviderForPlayer(Player player) {
        return ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() > 5 ? new Minecraft18HologramPacketProvider() : new Minecraft17HologramPacketProvider();
    }

    protected Collection<UUID> getViewers() {
        return this.viewers;
    }
}

