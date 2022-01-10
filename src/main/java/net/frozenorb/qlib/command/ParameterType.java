/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.command;

import java.util.List;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface ParameterType<T> {
    public T transform(CommandSender var1, String var2);

    public List<String> tabComplete(Player var1, Set<String> var2, String var3);
}

