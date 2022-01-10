/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageModifier
 *  org.bukkit.event.player.PlayerEvent
 */
package net.frozenorb.qlib.deathmessage.event;

import net.frozenorb.qlib.deathmessage.damage.Damage;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;

public final class CustomPlayerDamageEvent
extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final EntityDamageEvent cause;
    private Damage trackerDamage;

    public CustomPlayerDamageEvent(EntityDamageEvent cause) {
        super((Player)cause.getEntity());
        this.cause = cause;
    }

    public double getDamage() {
        return this.cause.getDamage(EntityDamageEvent.DamageModifier.BASE);
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public EntityDamageEvent getCause() {
        return this.cause;
    }

    public Damage getTrackerDamage() {
        return this.trackerDamage;
    }

    public void setTrackerDamage(Damage trackerDamage) {
        this.trackerDamage = trackerDamage;
    }
}

