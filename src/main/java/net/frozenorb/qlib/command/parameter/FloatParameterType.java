/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.command.parameter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Set;
import net.frozenorb.qlib.command.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FloatParameterType
implements ParameterType<Float> {
    @Override
    public Float transform(CommandSender sender, String value) {
        if (value.toLowerCase().contains("e")) {
            sender.sendMessage((Object)ChatColor.RED + value + " is not a valid number.");
            return null;
        }
        try {
            float parsed = Float.parseFloat(value);
            if (Float.isNaN(parsed) || !Float.isFinite(parsed)) {
                sender.sendMessage((Object)ChatColor.RED + value + " is not a valid number.");
                return null;
            }
            return Float.valueOf(parsed);
        }
        catch (NumberFormatException exception) {
            sender.sendMessage((Object)ChatColor.RED + value + " is not a valid number.");
            return null;
        }
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return ImmutableList.of();
    }
}

