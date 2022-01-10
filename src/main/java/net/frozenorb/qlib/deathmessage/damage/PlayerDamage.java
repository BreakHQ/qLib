/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.deathmessage.damage;

import java.util.UUID;
import net.frozenorb.qlib.deathmessage.damage.Damage;

public abstract class PlayerDamage
extends Damage {
    private final UUID damager;

    public PlayerDamage(UUID damaged, double damage, UUID damager) {
        super(damaged, damage);
        this.damager = damager;
    }

    public UUID getDamager() {
        return this.damager;
    }
}

