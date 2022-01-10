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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class PaginatedOutput<T> {
    private final int resultsPerPage;

    public PaginatedOutput() {
        this(9);
    }

    public PaginatedOutput(int resultsPerPage) {
        Preconditions.checkArgument((resultsPerPage > 0 ? 1 : 0) != 0);
        this.resultsPerPage = resultsPerPage;
    }

    public abstract String getHeader(int var1, int var2);

    public abstract String format(T var1, int var2);

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
        sender.sendMessage(this.getHeader(page, maxPages));
        for (int i = this.resultsPerPage * (page - 1); i < this.resultsPerPage * page && i < results.size(); ++i) {
            sender.sendMessage(this.format(results.get(i), i));
        }
    }
}

