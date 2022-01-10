/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package net.frozenorb.qlib.librato;

import com.librato.metrics.LibratoBatch;
import java.beans.ConstructorProperties;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FastLibratoPostEvent
extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private LibratoBatch batch;

    public HandlerList getHandlers() {
        return handlerList;
    }

    @ConstructorProperties(value={"batch"})
    public FastLibratoPostEvent(LibratoBatch batch) {
        this.batch = batch;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public LibratoBatch getBatch() {
        return this.batch;
    }
}

