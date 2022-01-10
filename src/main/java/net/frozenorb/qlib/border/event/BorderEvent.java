/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package net.frozenorb.qlib.border.event;

import net.frozenorb.qlib.border.Border;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BorderEvent
extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private Border border;

    public BorderEvent(Border border) {
        this.border = border;
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public Border getBorder() {
        return this.border;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

