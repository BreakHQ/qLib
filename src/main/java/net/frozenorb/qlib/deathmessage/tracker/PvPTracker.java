/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.WordUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.inventory.ItemStack
 */
package net.frozenorb.qlib.deathmessage.tracker;

import java.util.UUID;
import net.frozenorb.qlib.deathmessage.FrozenDeathMessageHandler;
import net.frozenorb.qlib.deathmessage.damage.PlayerDamage;
import net.frozenorb.qlib.deathmessage.event.CustomPlayerDamageEvent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public final class PvPTracker
implements Listener {
    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (!(event.getCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }
        EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent)event.getCause();
        Entity damager = damageByEntityEvent.getDamager();
        if (damager instanceof Player) {
            Player damaged = event.getPlayer();
            event.setTrackerDamage(new PvPDamage(damaged.getUniqueId(), event.getDamage(), (Player)damager));
        }
    }

    public static class PvPDamage
    extends PlayerDamage {
        private final String itemString;

        public PvPDamage(UUID damaged, double damage, Player damager) {
            super(damaged, damage, damager.getUniqueId());
            ItemStack hand = damager.getItemInHand();
            this.itemString = hand.getType() == Material.AIR ? "their fists" : (hand.getItemMeta().hasDisplayName() ? ChatColor.stripColor((String)hand.getItemMeta().getDisplayName()) : WordUtils.capitalizeFully((String)hand.getType().name().replace('_', ' ')));
        }

        @Override
        public String getDeathMessage(UUID getFor) {
            return PvPDamage.wrapName(this.getDamaged(), getFor) + (Object)ChatColor.YELLOW + " was slain by " + PvPDamage.wrapName(this.getDamager(), getFor) + (Object)ChatColor.YELLOW + (!FrozenDeathMessageHandler.getConfiguration().hideWeapons() ? " using " + (Object)ChatColor.RED + this.itemString.trim() : "") + (Object)ChatColor.YELLOW + ".";
        }
    }
}

