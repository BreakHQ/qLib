/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package net.frozenorb.qlib.event;

import java.beans.ConstructorProperties;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HalfHourEvent
extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private int hour;
    private int minute;

    public HandlerList getHandlers() {
        return handlerList;
    }

    @ConstructorProperties(value={"hour", "minute"})
    public HalfHourEvent(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }
}

