/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package net.frozenorb.qlib.librato;

import com.google.common.primitives.Ints;
import com.librato.metrics.LibratoBatch;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import net.frozenorb.qlib.librato.FastLibratoPostEvent;
import net.frozenorb.qlib.librato.LibratoPostEvent;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.TPSUtils;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LibratoListener
implements Listener {
    @EventHandler
    public void onLibratoPost(LibratoPostEvent event) {
        int afkPlayersOnline = 0;
        int staffOnline = 0;
        int afkStaffOnline = 0;
        for (Player player : qLib.getInstance().getServer().getOnlinePlayers()) {
            boolean afk;
            boolean bl = afk = System.currentTimeMillis() - ((CraftPlayer)player).getHandle().x() > TimeUnit.MINUTES.toMillis(5L);
            if (afk) {
                ++afkPlayersOnline;
            }
            if (!player.hasPermission("basic.staff")) continue;
            ++staffOnline;
            if (!afk) continue;
            ++afkStaffOnline;
        }
        LibratoBatch batch = event.getBatch();
        batch.addGaugeMeasurement("bukkit.general.players.afk", afkPlayersOnline);
        batch.addGaugeMeasurement("bukkit.general.players.staff", staffOnline);
        batch.addGaugeMeasurement("bukkit.general.players.staff.afk", afkStaffOnline);
        batch.addGaugeMeasurement("bukkit.general.slots", qLib.getInstance().getServer().getMaxPlayers());
        batch.addGaugeMeasurement("bukkit.general.tps", TPSUtils.getTPS());
    }

    @EventHandler
    public void onFastLibratoPost(FastLibratoPostEvent event) {
        int playersOnline = 0;
        ArrayList<Integer> latencies = new ArrayList<Integer>();
        for (Player player : qLib.getInstance().getServer().getOnlinePlayers()) {
            ++playersOnline;
            int playerLatency = ((CraftPlayer)player).getHandle().ping;
            if (playerLatency <= 0 || playerLatency > 100000) continue;
            latencies.add(playerLatency);
        }
        LibratoBatch batch = event.getBatch();
        batch.addGaugeMeasurement("bukkit.general.players.online", playersOnline);
        batch.addGaugeMeasurement("bukkit.general.tps", TPSUtils.getTPS());
        latencies.sort(Ints::compare);
        int totalLatency = 0;
        int lower20Latency = 0;
        int top20Latency = 0;
        int topBottomSize = latencies.size() / 5;
        for (int i = 0; i < latencies.size(); ++i) {
            int latency = (Integer)latencies.get(i);
            totalLatency += latency;
            if (i < topBottomSize) {
                lower20Latency += latency;
                continue;
            }
            if (i < latencies.size() - topBottomSize) continue;
            top20Latency += latency;
        }
        double avgLatency = latencies.isEmpty() ? 0.0 : (double)(totalLatency / latencies.size());
        double lower20AvgLatency = topBottomSize == 0 ? 0.0 : (double)(lower20Latency / topBottomSize);
        double top20AvgLatency = topBottomSize == 0 ? 0.0 : (double)(top20Latency / topBottomSize);
        batch.addGaugeMeasurement("bukkit.experimental.latency.avg", avgLatency);
        batch.addGaugeMeasurement("bukkit.experimental.latency.top20Avg", top20AvgLatency);
        batch.addGaugeMeasurement("bukkit.experimental.latency.lower20Avg", lower20AvgLatency);
    }
}

