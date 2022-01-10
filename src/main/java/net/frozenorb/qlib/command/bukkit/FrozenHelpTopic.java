/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.help.HelpTopic
 */
package net.frozenorb.qlib.command.bukkit;

import java.util.Set;
import net.frozenorb.qlib.command.CommandNode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

public class FrozenHelpTopic
extends HelpTopic {
    private CommandNode node;

    public FrozenHelpTopic(CommandNode node, Set<String> aliases) {
        this.node = node;
        this.name = "/" + node.getName();
        String description = node.getDescription();
        this.shortText = description.length() < 32 ? description : description.substring(0, 32);
        StringBuilder sb = new StringBuilder();
        sb.append((Object)ChatColor.GOLD);
        sb.append("Description: ");
        sb.append((Object)ChatColor.WHITE);
        sb.append(node.getDescription());
        sb.append("\n");
        sb.append((Object)ChatColor.GOLD);
        sb.append("Usage: ");
        sb.append((Object)ChatColor.WHITE);
        sb.append(node.getUsageForHelpTopic());
        if (aliases != null && aliases.size() > 0) {
            sb.append("\n");
            sb.append((Object)ChatColor.GOLD);
            sb.append("Aliases: ");
            sb.append((Object)ChatColor.WHITE);
            sb.append(StringUtils.join(aliases, (String)", "));
        }
        this.fullText = sb.toString();
    }

    public boolean canSee(CommandSender commandSender) {
        return this.node.canUse(commandSender);
    }
}

