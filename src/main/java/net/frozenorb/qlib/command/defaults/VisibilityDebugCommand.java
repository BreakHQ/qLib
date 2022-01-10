/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.com.google.common.collect.Iterables
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.command.defaults;

import java.util.List;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.visibility.FrozenVisibilityHandler;
import net.minecraft.util.com.google.common.collect.Iterables;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VisibilityDebugCommand {
    @Command(names={"visibilitydebug", "debugvisibility", "visdebug", "cansee"}, permission="")
    public static void visibilityDebug(Player sender, @Param(name="viewer") Player viewer, @Param(name="target") Player target) {
        boolean bukkit;
        List<String> lines = FrozenVisibilityHandler.getDebugInfo(target, viewer);
        for (String debugLine : lines) {
            sender.sendMessage(debugLine);
        }
        boolean shouldBeAbleToSee = false;
        if (!((String)Iterables.getLast(lines)).contains("cannot")) {
            shouldBeAbleToSee = true;
        }
        if (shouldBeAbleToSee != (bukkit = viewer.canSee(target))) {
            sender.sendMessage(ChatColor.DARK_RED.toString() + (Object)ChatColor.BOLD + "Updating was not done correctly: " + viewer.getName() + " should be able to see " + target.getName() + " but cannot.");
        } else {
            sender.sendMessage((Object)ChatColor.GREEN + "Bukkit currently respects this result.");
        }
    }
}

