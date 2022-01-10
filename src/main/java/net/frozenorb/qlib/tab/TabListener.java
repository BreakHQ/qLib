/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.qlib.tab;

import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.tab.FrozenTabHandler;
import net.frozenorb.qlib.tab.TabLayout;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TabListener
implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        new BukkitRunnable(){

            public void run() {
                FrozenTabHandler.addPlayer(event.getPlayer());
            }
        }.runTaskLater((Plugin)qLib.getInstance(), 10L);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        FrozenTabHandler.removePlayer(event.getPlayer());
        TabLayout.remove(event.getPlayer());
    }
}

