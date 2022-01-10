/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.scoreboard;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.scoreboard.FrozenScoreboard;
import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import net.frozenorb.qlib.scoreboard.ScoreboardListener;
import net.frozenorb.qlib.scoreboard.ScoreboardThread;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class FrozenScoreboardHandler {
    private static Map<String, FrozenScoreboard> boards = new ConcurrentHashMap<String, FrozenScoreboard>();
    private static ScoreboardConfiguration configuration = null;
    private static boolean initiated = false;
    private static int updateInterval = 2;

    private FrozenScoreboardHandler() {
    }

    public static void init() {
        if (qLib.getInstance().getConfig().getBoolean("disableScoreboard", false)) {
            return;
        }
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated = true;
        new ScoreboardThread().start();
        qLib.getInstance().getServer().getPluginManager().registerEvents((Listener)new ScoreboardListener(), (Plugin)qLib.getInstance());
    }

    protected static void create(Player player) {
        if (configuration != null) {
            boards.put(player.getName(), new FrozenScoreboard(player));
        }
    }

    protected static void updateScoreboard(Player player) {
        FrozenScoreboard board = boards.get(player.getName());
        if (board != null) {
            board.update();
        }
    }

    protected static void remove(Player player) {
        boards.remove(player.getName());
    }

    public static ScoreboardConfiguration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(ScoreboardConfiguration configuration) {
        FrozenScoreboardHandler.configuration = configuration;
    }

    public static int getUpdateInterval() {
        return updateInterval;
    }

    public static void setUpdateInterval(int updateInterval) {
        FrozenScoreboardHandler.updateInterval = updateInterval;
    }
}

