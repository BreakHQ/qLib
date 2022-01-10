/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.visibility;

import net.frozenorb.qlib.visibility.OverrideAction;
import org.bukkit.entity.Player;

public interface OverrideHandler {
    public OverrideAction getAction(Player var1, Player var2);
}

