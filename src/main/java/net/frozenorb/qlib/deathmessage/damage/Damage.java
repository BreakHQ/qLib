/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.deathmessage.damage;

import java.util.UUID;
import net.frozenorb.qlib.deathmessage.DeathMessageConfiguration;
import net.frozenorb.qlib.deathmessage.FrozenDeathMessageHandler;

public abstract class Damage {
    private final UUID damaged;
    private final double damage;
    private final long time;

    public Damage(UUID damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    public static String wrapName(UUID player, UUID wrapFor) {
        DeathMessageConfiguration configuration = FrozenDeathMessageHandler.getConfiguration();
        return configuration.formatPlayerName(player, wrapFor);
    }

    public static String wrapName(UUID player) {
        DeathMessageConfiguration configuration = FrozenDeathMessageHandler.getConfiguration();
        return configuration.formatPlayerName(player);
    }

    public abstract String getDeathMessage(UUID var1);

    public long getTimeAgoMillis() {
        return System.currentTimeMillis() - this.time;
    }

    public UUID getDamaged() {
        return this.damaged;
    }

    public double getDamage() {
        return this.damage;
    }

    public long getTime() {
        return this.time;
    }
}

