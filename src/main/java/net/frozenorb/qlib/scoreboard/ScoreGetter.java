/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.scoreboard;

import net.frozenorb.qlib.util.LinkedList;
import org.bukkit.entity.Player;

public interface ScoreGetter {
    public void getScores(LinkedList<String> var1, Player var2);
}

