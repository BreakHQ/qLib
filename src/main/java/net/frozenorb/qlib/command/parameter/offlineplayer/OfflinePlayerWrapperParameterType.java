/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.command.parameter.offlineplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.qlib.command.parameter.offlineplayer.OfflinePlayerWrapper;
import net.frozenorb.qlib.visibility.FrozenVisibilityHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OfflinePlayerWrapperParameterType
implements ParameterType<OfflinePlayerWrapper> {
    @Override
    public OfflinePlayerWrapper transform(CommandSender sender, String source) {
        return new OfflinePlayerWrapper(source);
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

