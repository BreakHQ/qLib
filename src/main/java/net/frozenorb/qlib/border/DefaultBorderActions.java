/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.util.Vector
 */
package net.frozenorb.qlib.border;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.frozenorb.qlib.border.Border;
import net.frozenorb.qlib.border.event.BorderChangeEvent;
import net.frozenorb.qlib.border.event.PlayerExitBorderEvent;
import net.frozenorb.qlib.cuboid.Cuboid;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public final class DefaultBorderActions {
    static Map<UUID, Long> lastMessaged = Maps.newHashMap();
    public static final Consumer<PlayerExitBorderEvent> CANCEL_EXIT = playerExitBorderEvent -> {
        Player player = playerExitBorderEvent.getPlayer();
        playerExitBorderEvent.setCancelled(true);
        if (!lastMessaged.containsKey(player.getUniqueId()) || System.currentTimeMillis() - lastMessaged.get(player.getUniqueId()) > TimeUnit.SECONDS.toMillis(1L)) {
            player.sendMessage((Object)ChatColor.RED + "You have reached the border!");
            lastMessaged.put(player.getUniqueId(), System.currentTimeMillis());
        }
    };
    public static final Consumer<PlayerExitBorderEvent> PUSHBACK_ON_EXIT = playerExitBorderEvent -> {
        playerExitBorderEvent.getPlayer().setMetadata("Border-Pushback", (MetadataValue)new FixedMetadataValue((Plugin)qLib.getInstance(), (Object)System.currentTimeMillis()));
        new BukkitRunnable((PlayerExitBorderEvent)((Object)playerExitBorderEvent)){
            final /* synthetic */ PlayerExitBorderEvent val$playerExitBorderEvent;
            {
                this.val$playerExitBorderEvent = playerExitBorderEvent;
            }

            public void run() {
                Border border = this.val$playerExitBorderEvent.getBorder();
                Player player = this.val$playerExitBorderEvent.getPlayer();
                Location location = this.val$playerExitBorderEvent.getTo();
                Cuboid cuboid = border.getPhysicalBounds();
                double validX = location.getX();
                double validZ = location.getZ();
                if (location.getBlockX() + 2 > cuboid.getUpperX()) {
                    validX = cuboid.getUpperX() - 3;
                } else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
                    validX = cuboid.getLowerX() + 4;
                }
                if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
                    validZ = cuboid.getUpperZ() - 3;
                } else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
                    validZ = cuboid.getLowerZ() + 4;
                }
                Location validLoc = new Location(location.getWorld(), validX, location.getY(), validZ);
                Vector velocity = validLoc.toVector().subtract(this.val$playerExitBorderEvent.getTo().toVector()).multiply(0.18);
                if (player.getVehicle() != null) {
                    player.getVehicle().setVelocity(velocity);
                } else {
                    player.setVelocity(velocity);
                }
                if (!lastMessaged.containsKey(player.getUniqueId()) || System.currentTimeMillis() - lastMessaged.get(player.getUniqueId()) > TimeUnit.SECONDS.toMillis(1L)) {
                    player.sendMessage((Object)ChatColor.RED + "You have reached the border!");
                    lastMessaged.put(player.getUniqueId(), System.currentTimeMillis());
                }
            }
        }.runTask((Plugin)qLib.getInstance());
    };
    public static final Consumer<BorderChangeEvent> ENSURE_PLAYERS_IN_BORDER = borderChangeEvent -> {
        Border border = borderChangeEvent.getBorder();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() != border.getOrigin().getWorld() || border.contains(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) continue;
            Location location = border.correctLocation(player.getLocation());
            if (player.getVehicle() != null) {
                Entity vehicle = player.getVehicle();
                player.leaveVehicle();
                vehicle.teleport(location);
                player.teleport(location);
                vehicle.setPassenger((Entity)player);
                continue;
            }
            player.teleport(location);
        }
    };
}

