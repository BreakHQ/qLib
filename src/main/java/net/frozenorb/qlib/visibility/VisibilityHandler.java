/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.visibility;

import net.frozenorb.qlib.visibility.VisibilityAction;
import org.bukkit.entity.Player;

public interface VisibilityHandler {
    public VisibilityAction getAction(Player var1, Player var2);
}

