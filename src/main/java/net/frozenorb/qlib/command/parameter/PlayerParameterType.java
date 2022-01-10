/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.command.parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.qlib.visibility.FrozenVisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerParameterType
implements ParameterType<Player> {
    @Override
    public Player transform(CommandSender sender, String value) {
        if (sender instanceof Player && (value.equalsIgnoreCase("self") || value.equals(""))) {
            return (Player)sender;
        }
        Player player = Bukkit.getServer().getPlayer(value);
        if (player == null || sender instanceof Player && !FrozenVisibilityHandler.treatAsOnline(player, (Player)sender)) {
            sender.sendMessage((Object)ChatColor.RED + "No player with the name \"" + value + "\" found.");
            return null;
        }
        return player;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions = new ArrayList<String>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!FrozenVisibilityHandler.treatAsOnline(player, sender)) continue;
            completions.add(player.getName());
        }
        return completions;
    }
}

