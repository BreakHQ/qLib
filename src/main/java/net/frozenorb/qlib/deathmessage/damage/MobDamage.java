/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 */
package net.frozenorb.qlib.deathmessage.damage;

import java.util.UUID;
import net.frozenorb.qlib.deathmessage.damage.Damage;
import org.bukkit.entity.EntityType;

public abstract class MobDamage
extends Damage {
    private final EntityType mobType;

    public MobDamage(UUID damaged, double damage, EntityType mobType) {
        super(damaged, damage);
        this.mobType = mobType;
    }

    public EntityType getMobType() {
        return this.mobType;
    }
}

