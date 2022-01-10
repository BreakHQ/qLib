/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package net.frozenorb.qlib.util;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class FancyPaginatedOutput<T> {
    private final int resultsPerPage;

    public FancyPaginatedOutput() {
        this(9);
    }

    public FancyPaginatedOutput(int resultsPerPage) {
        Preconditions.checkArgument((resultsPerPage > 0 ? 1 : 0) != 0);
        this.resultsPerPage = resultsPerPage;
    }

    public abstract FancyMessage getHeader(int var1, int var2);

    public abstract FancyMessage format(T var1, int var2);

    public final void display(CommandSender sender, int page, Collection<? extends T> results) {
        this.display(sender, page, (List<? extends T>)new ArrayList<T>(results));
    }

    public final void display(CommandSender sender, int page, List<? extends T> results) {
        if (results.size() == 0) {
            sender.sendMessage((Object)ChatColor.RED + "No entries found.");
            return;
        }
        int maxPages = results.size() / this.resultsPerPage + 1;
        if (page <= 0 || page > maxPages) {
            sender.sendMessage((Object)ChatColor.RED + "Page " + (Object)ChatColor.YELLOW + page + (Object)ChatColor.RED + " is out of bounds. (" + (Object)ChatColor.YELLOW + "1 - " + maxPages + (Object)ChatColor.RED + ")");
            return;
        }
        this.getHeader(page, maxPages).send(sender);
        for (int i = this.resultsPerPage * (page - 1); i < this.resultsPerPage * page && i < results.size(); ++i) {
            this.format(results.get(i), i).send(sender);
        }
    }
}

