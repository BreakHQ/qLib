/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.player.PlayerEvent
 */
package net.frozenorb.qlib.protocol.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerOpenInventoryEvent
extends PlayerEvent {
    private static HandlerList handlerList = new HandlerList();

    public PlayerOpenInventoryEvent(Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

