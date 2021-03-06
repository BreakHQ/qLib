/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerChangedWorldEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.qlib.hologram;

import net.frozenorb.qlib.hologram.BaseHologram;
import net.frozenorb.qlib.hologram.Hologram;
import net.frozenorb.qlib.hologram.HologramRegistry;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HologramListener
implements Listener {
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Bukkit.getScheduler().runTaskLater((Plugin)qLib.getInstance(), () -> {
            for (Hologram hologram : HologramRegistry.getHolograms()) {
                BaseHologram hologram1 = (BaseHologram)hologram;
                if (hologram1.getViewers() != null && !hologram1.getViewers().contains(event.getPlayer().getUniqueId()) || !hologram1.getLocation().getWorld().equals((Object)event.getPlayer().getWorld()) || !(hologram.getLocation().distanceSquared(event.getPlayer().getLocation()) <= 1600.0)) continue;
                hologram1.show(event.getPlayer());
            }
        }, 20L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }
        for (Hologram hologram : HologramRegistry.getHolograms()) {
            BaseHologram hologram1 = (BaseHologram)hologram;
            if (hologram1.getViewers() != null && !hologram1.getViewers().contains(event.getPlayer().getUniqueId()) || !hologram1.getLocation().getWorld().equals((Object)event.getPlayer().getWorld())) continue;
            if (!hologram1.currentWatchers.contains(player.getUniqueId()) && hologram.getLocation().distanceSquared(player.getLocation()) <= 1600.0) {
                hologram1.show(player);
                continue;
            }
            if (!hologram1.currentWatchers.contains(player.getUniqueId()) || !(hologram.getLocation().distanceSquared(player.getLocation()) > 1600.0)) continue;
            hologram1.destroy0(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Hologram hologram : HologramRegistry.getHolograms()) {
            BaseHologram hologram1 = (BaseHologram)hologram;
            if (hologram1.getViewers() != null && !hologram1.getViewers().contains(event.getPlayer().getUniqueId()) || !hologram1.getLocation().getWorld().equals((Object)event.getPlayer().getWorld())) continue;
            hologram1.show(event.getPlayer());
        }
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        new BukkitRunnable(){

            public void run() {
                for (Hologram hologram : HologramRegistry.getHolograms()) {
                    BaseHologram hologram1 = (BaseHologram)hologram;
                    hologram1.destroy0(event.getPlayer());
                    if (hologram1.getViewers() != null && !hologram1.getViewers().contains(event.getPlayer().getUniqueId()) || !hologram1.getLocation().getWorld().equals((Object)event.getPlayer().getWorld())) continue;
                    hologram1.show(event.getPlayer());
                }
            }
        }.runTaskLater((Plugin)qLib.getInstance(), 10L);
    }
}

