/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package net.frozenorb.qlib.autoreboot.commands;

import java.util.concurrent.TimeUnit;
import net.frozenorb.qlib.autoreboot.AutoRebootHandler;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AutoRebootCommand {
    @Command(names={"reboot"}, permission="server.reboot")
    public static void reboot(CommandSender sender, @Param(name="time") String unparsedTime) {
        try {
            unparsedTime = unparsedTime.toLowerCase();
            int time = TimeUtils.parseTime(unparsedTime);
            AutoRebootHandler.rebootServer(time, TimeUnit.SECONDS);
            sender.sendMessage((Object)ChatColor.YELLOW + "Started auto reboot.");
        }
        catch (Exception ex) {
            sender.sendMessage((Object)ChatColor.RED + ex.getMessage());
        }
    }

    @Command(names={"reboot cancel"}, permission="server.reboot")
    public static void rebootCancel(CommandSender sender) {
        if (!AutoRebootHandler.isRebooting()) {
            sender.sendMessage((Object)ChatColor.RED + "No reboot has been scheduled.");
        } else {
            AutoRebootHandler.cancelReboot();
            qLib.getInstance().getServer().broadcastMessage((Object)ChatColor.RED + "\u26a0 " + ChatColor.DARK_RED.toString() + (Object)ChatColor.STRIKETHROUGH + "------------------------" + (Object)ChatColor.RED + " \u26a0");
            qLib.getInstance().getServer().broadcastMessage((Object)ChatColor.RED + "The server reboot has been cancelled.");
            qLib.getInstance().getServer().broadcastMessage((Object)ChatColor.RED + "\u26a0 " + ChatColor.DARK_RED.toString() + (Object)ChatColor.STRIKETHROUGH + "------------------------" + (Object)ChatColor.RED + " \u26a0");
        }
    }
}

