/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 */
package net.frozenorb.qlib.deathmessage.tracker;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.qlib.deathmessage.FrozenDeathMessageHandler;
import net.frozenorb.qlib.deathmessage.damage.Damage;
import net.frozenorb.qlib.deathmessage.damage.PlayerDamage;
import net.frozenorb.qlib.deathmessage.event.CustomPlayerDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public final class VoidTracker
implements Listener {
    @EventHandler(priority=EventPriority.LOW)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.VOID) {
            return;
        }
        List<Damage> record = FrozenDeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;
        for (Damage damage : record) {
            if (damage instanceof VoidDamage || damage instanceof VoidDamageByPlayer || !(damage instanceof PlayerDamage) || knocker != null && damage.getTime() <= knockerTime) continue;
            knocker = damage;
            knockerTime = damage.getTime();
        }
        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
            event.setTrackerDamage(new VoidDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(), ((PlayerDamage)knocker).getDamager()));
        } else {
            event.setTrackerDamage(new VoidDamage(event.getPlayer().getUniqueId(), event.getDamage()));
        }
    }

    public static class VoidDamageByPlayer
    extends PlayerDamage {
        public VoidDamageByPlayer(UUID damaged, double damage, UUID damager) {
            super(damaged, damage, damager);
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return VoidDamageByPlayer.wrapName(this.getDamaged(), getFor) + (Object)ChatColor.YELLOW + " fell into the void thanks to " + VoidDamageByPlayer.wrapName(this.getDamager(), getFor) + (Object)ChatColor.YELLOW + ".";
        }
    }

    public static class VoidDamage
    extends Damage {
        public VoidDamage(UUID damaged, double damage) {
            super(damaged, damage);
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return VoidDamage.wrapName(this.getDamaged(), getFor) + (Object)ChatColor.YELLOW + " fell into the void.";
        }
    }
}

