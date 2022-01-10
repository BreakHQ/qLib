/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.autoreboot;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.frozenorb.qlib.autoreboot.listeners.AutoRebootListener;
import net.frozenorb.qlib.autoreboot.tasks.ServerRebootTask;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.qLib;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class AutoRebootHandler {
    private static List<Integer> rebootTimes;
    private static boolean initiated;
    private static ServerRebootTask serverRebootTask;

    private AutoRebootHandler() {
    }

    public static void init() {
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated = true;
        FrozenCommandHandler.registerPackage((Plugin)qLib.getInstance(), "net.frozenorb.qlib.autoreboot.commands");
        rebootTimes = ImmutableList.copyOf((Collection)qLib.getInstance().getConfig().getIntegerList("AutoRebootTimes"));
        qLib.getInstance().getServer().getPluginManager().registerEvents((Listener)new AutoRebootListener(), (Plugin)qLib.getInstance());
    }

    @Deprecated
    public static void rebootServer(int seconds) {
        AutoRebootHandler.rebootServer(seconds, TimeUnit.SECONDS);
    }

    public static void rebootServer(int timeUnitAmount, TimeUnit timeUnit) {
        if (serverRebootTask != null) {
            throw new IllegalStateException("Reboot already in progress");
        }
        serverRebootTask = new ServerRebootTask(timeUnitAmount, timeUnit);
        serverRebootTask.runTaskTimer((Plugin)qLib.getInstance(), 20L, 20L);
    }

    public static boolean isRebooting() {
        return serverRebootTask != null;
    }

    public static int getRebootSecondsRemaining() {
        if (serverRebootTask == null) {
            return -1;
        }
        return serverRebootTask.getSecondsRemaining();
    }

    public static void cancelReboot() {
        if (serverRebootTask != null) {
            serverRebootTask.cancel();
            serverRebootTask = null;
        }
    }

    public static List<Integer> getRebootTimes() {
        return rebootTimes;
    }

    public static boolean isInitiated() {
        return initiated;
    }

    static {
        initiated = false;
        serverRebootTask = null;
    }
}

