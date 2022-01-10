/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.command.parameter.filter;

import com.google.common.collect.ImmutableList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import net.frozenorb.qlib.command.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

abstract class BaseFilter
implements ParameterType<String> {
    protected final Set<Pattern> bannedPatterns = new HashSet<Pattern>();

    BaseFilter() {
    }

    @Override
    public String transform(CommandSender sender, String value) {
        for (Pattern bannedPattern : this.bannedPatterns) {
            if (!bannedPattern.matcher(value).find()) continue;
            sender.sendMessage((Object)ChatColor.RED + "Command contains inappropriate content.");
            return null;
        }
        return value;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return ImmutableList.of();
    }
}

