/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package net.frozenorb.qlib.protocol.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerLaggedOutEvent
extends Event {
    private static HandlerList handlerList = new HandlerList();
    private int averagePing;

    public ServerLaggedOutEvent(int averagePing) {
        super(true);
        this.averagePing = averagePing;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

