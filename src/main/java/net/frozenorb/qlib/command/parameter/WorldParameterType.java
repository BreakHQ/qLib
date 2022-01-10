/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.command.parameter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.frozenorb.qlib.command.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldParameterType
implements ParameterType<World> {
    @Override
    public World transform(CommandSender sender, String value) {
        World world = Bukkit.getWorld((String)value);
        if (world == null) {
            sender.sendMessage((Object)ChatColor.RED + "No world with the name \"" + value + "\" found.");
            return null;
        }
        return world;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }
}

