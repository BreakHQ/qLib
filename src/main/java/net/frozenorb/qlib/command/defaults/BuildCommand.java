/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.command.defaults;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.qLib;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class BuildCommand {
    @Command(names={"build"}, permission="op")
    public static void build(Player sender) {
        if (sender.hasMetadata("build")) {
            sender.removeMetadata("build", (Plugin)qLib.getInstance());
        } else {
            sender.setMetadata("build", (MetadataValue)new FixedMetadataValue((Plugin)qLib.getInstance(), (Object)true));
        }
        sender.sendMessage((Object)ChatColor.YELLOW + "You are " + (sender.hasMetadata("build") ? (Object)ChatColor.GREEN + "now" : (Object)ChatColor.RED + "no longer") + (Object)ChatColor.YELLOW + " in build mode.");
    }
}

