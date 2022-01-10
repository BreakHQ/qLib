/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.combatlogger;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.qlib.combatlogger.CombatLogger;
import net.frozenorb.qlib.combatlogger.CombatLoggerConfiguration;
import net.frozenorb.qlib.combatlogger.CombatLoggerListener;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class FrozenCombatLoggerHandler {
    private static final Map<UUID, CombatLogger> combatLoggerMap = new HashMap<UUID, CombatLogger>();
    private static CombatLoggerConfiguration configuration = CombatLoggerConfiguration.DEFAULT_CONFIGURATION;
    private static boolean initiated = false;

    public static void init() {
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated = true;
        Bukkit.getPluginManager().registerEvents((Listener)new CombatLoggerListener(), (Plugin)qLib.getInstance());
    }

    public static Map<UUID, CombatLogger> getCombatLoggerMap() {
        return combatLoggerMap;
    }

    public static CombatLoggerConfiguration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(CombatLoggerConfiguration configuration) {
        FrozenCombatLoggerHandler.configuration = configuration;
    }

    static boolean isInitiated() {
        return initiated;
    }
}

