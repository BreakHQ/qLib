/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.qlib.autoreboot.tasks;

import java.util.concurrent.TimeUnit;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public final class ServerRebootTask
extends BukkitRunnable {
    private int secondsRemaining;
    private boolean wasWhitelisted;

    public ServerRebootTask(int timeUnitAmount, TimeUnit timeUnit) {
        this.secondsRemaining = (int)timeUnit.toSeconds(timeUnitAmount);
        this.wasWhitelisted = qLib.getInstance().getServer().hasWhitelist();
    }

    public void run() {
        if (this.secondsRemaining == 300) {
            qLib.getInstance().getServer().setWhitelist(true);
        } else if (this.secondsRemaining == 0) {
            qLib.getInstance().getServer().setWhitelist(this.wasWhitelisted);
            qLib.getInstance().getServer().shutdown();
        }
        switch (this.secondsRemaining) {
            case 5: 
            case 10: 
            case 15: 
            case 30: 
            case 60: 
            case 120: 
            case 180: 
            case 240: 
            case 300: {
                qLib.getInstance().getServer().broadcastMessage((Object)ChatColor.RED + "\u26a0 " + ChatColor.DARK_RED.toString() + (Object)ChatColor.STRIKETHROUGH + "------------------------" + (Object)ChatColor.RED + " \u26a0");
                qLib.getInstance().getServer().broadcastMessage((Object)ChatColor.RED + "Server rebooting in " + TimeUtils.formatIntoDetailedString(this.secondsRemaining) + ".");
                qLib.getInstance().getServer().broadcastMessage((Object)ChatColor.RED + "\u26a0 " + ChatColor.DARK_RED.toString() + (Object)ChatColor.STRIKETHROUGH + "------------------------" + (Object)ChatColor.RED + " \u26a0");
                break;
            }
        }
        --this.secondsRemaining;
    }

    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        Bukkit.setWhitelist((boolean)this.wasWhitelisted);
    }

    public int getSecondsRemaining() {
        return this.secondsRemaining;
    }
}

