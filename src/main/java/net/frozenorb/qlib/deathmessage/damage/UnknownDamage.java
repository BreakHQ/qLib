/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package net.frozenorb.qlib.deathmessage.damage;

import java.util.UUID;
import net.frozenorb.qlib.deathmessage.damage.Damage;
import org.bukkit.ChatColor;

public final class UnknownDamage
extends Damage {
    public UnknownDamage(UUID damaged, double damage) {
        super(damaged, damage);
    }

    @Override
    public String getDeathMessage(UUID getFor) {
        return UnknownDamage.wrapName(this.getDamaged(), getFor) + (Object)ChatColor.YELLOW + " died.";
    }
}

