/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_7_R4.EntityHuman
 *  net.minecraft.server.v1_7_R4.MinecraftServer
 *  net.minecraft.server.v1_7_R4.Packet
 *  net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy
 *  net.minecraft.server.v1_7_R4.PacketPlayOutEntityStatus
 *  net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 */
package net.frozenorb.qlib.util;

import java.lang.reflect.Field;
import java.util.HashSet;
import net.frozenorb.qlib.protocol.InventoryAdapter;
import net.frozenorb.qlib.protocol.PingAdapter;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.EntityUtils;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public final class PlayerUtils {
    private static Field STATUS_PACKET_ID_FIELD;
    private static Field STATUS_PACKET_STATUS_FIELD;
    private static Field SPAWN_PACKET_ID_FIELD;

    private PlayerUtils() {
    }

    public static void resetInventory(Player player) {
        PlayerUtils.resetInventory(player, null);
    }

    public static void resetInventory(Player player, GameMode gameMode) {
        player.setHealth(player.getMaxHealth());
        player.setFallDistance(0.0f);
        player.setFoodLevel(20);
        player.setSaturation(10.0f);
        player.setLevel(0);
        player.setExp(0.0f);
        if (!player.hasMetadata("modmode")) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        }
        player.setFireTicks(0);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        if (gameMode != null && player.getGameMode() != gameMode) {
            player.setGameMode(gameMode);
        }
    }

    public static Player getDamageSource(Entity damager) {
        Projectile projectile;
        Player playerDamager = null;
        if (damager instanceof Player) {
            playerDamager = (Player)damager;
        } else if (damager instanceof Projectile && (projectile = (Projectile)damager).getShooter() instanceof Player) {
            playerDamager = (Player)projectile.getShooter();
        }
        return playerDamager;
    }

    public static boolean hasOpenInventory(Player player) {
        return PlayerUtils.hasOwnInventoryOpen(player) || PlayerUtils.hasOtherInventoryOpen(player);
    }

    public static boolean hasOwnInventoryOpen(Player player) {
        return InventoryAdapter.getCurrentlyOpen().contains(player.getUniqueId());
    }

    public static boolean hasOtherInventoryOpen(Player player) {
        return ((CraftPlayer)player).getHandle().activeContainer.windowId != 0;
    }

    public static int getPing(Player player) {
        return ((CraftPlayer)player).getHandle().ping;
    }

    public static boolean isLagging(Player player) {
        return !PingAdapter.getLastReply().containsKey(player.getUniqueId()) || MinecraftServer.currentTick - PingAdapter.getLastReply().get(player.getUniqueId()) > 40;
    }

    public static void animateDeath(Player player) {
        int entityId = EntityUtils.getFakeEntityId();
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn((EntityHuman)((CraftPlayer)player).getHandle());
        PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();
        try {
            SPAWN_PACKET_ID_FIELD.set((Object)spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set((Object)statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set((Object)statusPacket, (byte)3);
            int radius = MinecraftServer.getServer().getPlayerList().d();
            HashSet<Player> sentTo = new HashSet<Player>();
            for (Entity entity : player.getNearbyEntities((double)radius, (double)radius, (double)radius)) {
                Player watcher;
                if (!(entity instanceof Player) || (watcher = (Player)entity).getUniqueId().equals(player.getUniqueId())) continue;
                ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)spawnPacket);
                ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)statusPacket);
                sentTo.add(watcher);
            }
            Bukkit.getScheduler().runTaskLater((Plugin)qLib.getInstance(), () -> {
                for (Player watcher : sentTo) {
                    ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutEntityDestroy(new int[]{entityId}));
                }
            }, 40L);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void animateDeath(Player player, Player watcher) {
        int entityId = EntityUtils.getFakeEntityId();
        PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn((EntityHuman)((CraftPlayer)player).getHandle());
        PacketPlayOutEntityStatus statusPacket = new PacketPlayOutEntityStatus();
        try {
            SPAWN_PACKET_ID_FIELD.set((Object)spawnPacket, entityId);
            STATUS_PACKET_ID_FIELD.set((Object)statusPacket, entityId);
            STATUS_PACKET_STATUS_FIELD.set((Object)statusPacket, (byte)3);
            ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)spawnPacket);
            ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)statusPacket);
            Bukkit.getScheduler().runTaskLater((Plugin)qLib.getInstance(), () -> ((CraftPlayer)watcher).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutEntityDestroy(new int[]{entityId})), 40L);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            STATUS_PACKET_ID_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("a");
            STATUS_PACKET_ID_FIELD.setAccessible(true);
            STATUS_PACKET_STATUS_FIELD = PacketPlayOutEntityStatus.class.getDeclaredField("b");
            STATUS_PACKET_STATUS_FIELD.setAccessible(true);
            SPAWN_PACKET_ID_FIELD = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
            SPAWN_PACKET_ID_FIELD.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}

