/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package net.frozenorb.qlib.scoreboard;

import net.frozenorb.qlib.scoreboard.FrozenScoreboardHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

final class ScoreboardListener
implements Listener {
    ScoreboardListener() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FrozenScoreboardHandler.create(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        FrozenScoreboardHandler.remove(event.getPlayer());
    }
}

