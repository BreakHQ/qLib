/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityShootBowEvent
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.deathmessage.tracker;

import java.util.UUID;
import net.frozenorb.qlib.deathmessage.damage.Damage;
import net.frozenorb.qlib.deathmessage.damage.MobDamage;
import net.frozenorb.qlib.deathmessage.damage.PlayerDamage;
import net.frozenorb.qlib.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.EntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public final class ArrowTracker
implements Listener {
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            event.getProjectile().setMetadata("ShotFromDistance", (MetadataValue)new FixedMetadataValue((Plugin)qLib.getInstance(), (Object)event.getProjectile().getLocation()));
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (!(event.getCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }
        EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent)event.getCause();
        if (!(damageByEntityEvent.getDamager() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow)damageByEntityEvent.getDamager();
        if (arrow.getShooter() instanceof Player) {
            Player shooter = (Player)arrow.getShooter();
            for (MetadataValue value : arrow.getMetadata("ShotFromDistance")) {
                Location shotFrom = (Location)value.value();
                double distance = shotFrom.distance(event.getPlayer().getLocation());
                event.setTrackerDamage(new ArrowDamageByPlayer(event.getPlayer().getUniqueId(), event.getDamage(), shooter.getUniqueId(), distance));
            }
        } else if (arrow.getShooter() != null) {
            if (arrow.getShooter() instanceof Entity) {
                event.setTrackerDamage(new ArrowDamageByMob(event.getPlayer().getUniqueId(), event.getDamage(), (Entity)arrow.getShooter()));
            }
        } else {
            event.setTrackerDamage(new ArrowDamage(event.getPlayer().getUniqueId(), event.getDamage()));
        }
    }

    public static class ArrowDamageByMob
    extends MobDamage {
        public ArrowDamageByMob(UUID damaged, double damage, Entity damager) {
            super(damaged, damage, damager.getType());
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return ArrowDamageByMob.wrapName(this.getDamaged(), getFor) + (Object)ChatColor.YELLOW + " was shot by a " + (Object)ChatColor.RED + EntityUtils.getName(this.getMobType()) + (Object)ChatColor.YELLOW + ".";
        }
    }

    public static class ArrowDamageByPlayer
    extends PlayerDamage {
        private final double distance;

        public ArrowDamageByPlayer(UUID damaged, double damage, UUID damager, double distance) {
            super(damaged, damage, damager);
            this.distance = distance;
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return ArrowDamageByPlayer.wrapName(this.getDamaged(), getFor) + (Object)ChatColor.YELLOW + " was shot by " + ArrowDamageByPlayer.wrapName(this.getDamager(), getFor) + (Object)ChatColor.YELLOW + " from " + (Object)ChatColor.BLUE + (int)this.distance + " blocks" + (Object)ChatColor.YELLOW + ".";
        }
    }

    public static class ArrowDamage
    extends Damage {
        public ArrowDamage(UUID damaged, double damage) {
            super(damaged, damage);
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return ArrowDamage.wrapName(this.getDamaged(), getFor) + (Object)ChatColor.YELLOW + " was shot.";
        }
    }
}

