/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.scoreboard;

import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.scoreboard.FrozenScoreboardHandler;
import org.bukkit.entity.Player;

final class ScoreboardThread
extends Thread {
    public ScoreboardThread() {
        super("qLib - Scoreboard Thread");
        this.setDaemon(true);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    public void run() {
        while (true) lbl-1000:
        // 5 sources

        {
            for (Player online : qLib.getInstance().getServer().getOnlinePlayers()) {
                try {
                    FrozenScoreboardHandler.updateScoreboard(online);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep((long)FrozenScoreboardHandler.getUpdateInterval() * 50L);
                ** continue;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            break;
        }
    }
}

