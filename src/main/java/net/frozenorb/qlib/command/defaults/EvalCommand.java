/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 */
package net.frozenorb.qlib.command.defaults;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class EvalCommand {
    @Command(names={"eval"}, permission="console", description="Evaluates a command")
    public static void eval(CommandSender sender, @Param(name="command", wildcard=true) String commandLine) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage((Object)ChatColor.RED + "This is a console-only utility command. It cannot be used from in-game.");
            return;
        }
        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)commandLine);
    }
}

