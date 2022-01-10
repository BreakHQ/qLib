/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.border.event;

import net.frozenorb.qlib.border.Border;
import net.frozenorb.qlib.border.event.PlayerBorderEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerEnterBorderEvent
extends PlayerBorderEvent {
    private final Location from;
    private final Location to;

    public PlayerEnterBorderEvent(Border border, Player player, Location from, Location to) {
        super(border, player);
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return this.from;
    }

    public Location getTo() {
        return this.to;
    }
}

