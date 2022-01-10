/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.qlib.librato;

import com.librato.metrics.BatchResult;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.PostResult;
import com.librato.metrics.Sanitizer;
import java.util.concurrent.TimeUnit;
import net.frozenorb.qlib.librato.FastLibratoPostEvent;
import net.frozenorb.qlib.qLib;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FastLibratoPostTask
extends BukkitRunnable {
    public void run() {
        Plugin hydrogenPlugin = qLib.getInstance().getServer().getPluginManager().getPlugin("Hydrogen");
        String serverName = hydrogenPlugin == null ? "unknown" : hydrogenPlugin.getConfig().getString("ServerName", "unknown").toLowerCase();
        LibratoBatch batch = new LibratoBatch(300, Sanitizer.NO_OP, 10L, TimeUnit.SECONDS, "qLib", qLib.getInstance().getLibratoPoster());
        qLib.getInstance().getServer().getPluginManager().callEvent((Event)new FastLibratoPostEvent(batch));
        BatchResult result = batch.post(serverName, System.currentTimeMillis() / 1000L);
        if (!result.success()) {
            for (PostResult post : result.getFailedPosts()) {
                qLib.getInstance().getLogger().warning("Could not POST to Librato: " + post);
            }
        }
    }
}

