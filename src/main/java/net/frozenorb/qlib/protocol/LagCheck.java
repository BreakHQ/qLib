/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.com.google.common.collect.ImmutableList
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.qlib.protocol;

import java.util.Collection;
import net.frozenorb.qlib.protocol.PingAdapter;
import net.frozenorb.qlib.protocol.event.ServerLaggedOutEvent;
import net.frozenorb.qlib.util.PlayerUtils;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

public class LagCheck
extends BukkitRunnable {
    public void run() {
        ImmutableList players = ImmutableList.copyOf((Collection)Bukkit.getOnlinePlayers());
        if (players.size() >= 100) {
            int playersLagging = 0;
            for (Player player : players) {
                if (!PlayerUtils.isLagging(player)) continue;
                ++playersLagging;
            }
            double percentage = playersLagging * 100 / players.size();
            if (Math.abs(percentage) >= 30.0) {
                Bukkit.getPluginManager().callEvent((Event)new ServerLaggedOutEvent(PingAdapter.getAveragePing()));
            }
        }
    }
}

