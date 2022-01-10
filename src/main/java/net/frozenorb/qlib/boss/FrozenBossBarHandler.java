/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.md_5.bungee.api.ChatColor
 *  net.minecraft.server.v1_7_R4.DataWatcher
 *  net.minecraft.server.v1_7_R4.Entity
 *  net.minecraft.server.v1_7_R4.EntityPlayer
 *  net.minecraft.server.v1_7_R4.MinecraftServer
 *  net.minecraft.server.v1_7_R4.Packet
 *  net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy
 *  net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata
 *  net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport
 *  net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving
 *  net.minecraft.server.v1_7_R4.WatchableObject
 *  net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.boss;

import com.google.common.base.Preconditions;
import java.beans.ConstructorProperties;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.EntityUtils;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.DataWatcher;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.WatchableObject;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class FrozenBossBarHandler {
    private static boolean initiated = false;
    private static Map<UUID, BarData> displaying = new HashMap<UUID, BarData>();
    private static Map<UUID, Integer> lastUpdatedPosition = new HashMap<UUID, Integer>();
    private static Field spawnPacketAField = null;
    private static Field spawnPacketBField = null;
    private static Field spawnPacketCField = null;
    private static Field spawnPacketDField = null;
    private static Field spawnPacketEField = null;
    private static Field spawnPacketLField = null;
    private static Field metadataPacketAField = null;
    private static Field metadataPacketBField = null;
    private static TObjectIntHashMap classToIdMap = null;

    public static void init() {
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated = true;
        Bukkit.getScheduler().runTaskTimer((Plugin)qLib.getInstance(), () -> {
            for (UUID uuid : displaying.keySet()) {
                int updateTicks;
                Player player = Bukkit.getPlayer((UUID)uuid);
                if (player == null) {
                    return;
                }
                int n = updateTicks = ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() != 47 ? 60 : 3;
                if (lastUpdatedPosition.containsKey(player.getUniqueId()) && MinecraftServer.currentTick - lastUpdatedPosition.get(player.getUniqueId()) < updateTicks) {
                    return;
                }
                FrozenBossBarHandler.updatePosition(player);
                lastUpdatedPosition.put(player.getUniqueId(), MinecraftServer.currentTick);
            }
        }, 1L, 1L);
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                FrozenBossBarHandler.removeBossBar(event.getPlayer());
            }

            @EventHandler
            public void onPlayerTeleport(PlayerTeleportEvent event) {
                Player player = event.getPlayer();
                if (!displaying.containsKey(player.getUniqueId())) {
                    return;
                }
                BarData data = (BarData)displaying.get(player.getUniqueId());
                String message = data.message;
                float health = data.health;
                FrozenBossBarHandler.removeBossBar(player);
                FrozenBossBarHandler.setBossBar(player, message, health);
            }
        }, (Plugin)qLib.getInstance());
    }

    public static void setBossBar(Player player, String message, float health) {
        try {
            if (message == null) {
                FrozenBossBarHandler.removeBossBar(player);
                return;
            }
            Preconditions.checkArgument((health >= 0.0f && health <= 1.0f ? 1 : 0) != 0, (Object)"Health must be between 0 and 1");
            if (message.length() > 64) {
                message = message.substring(0, 64);
            }
            message = ChatColor.translateAlternateColorCodes((char)'&', (String)message);
            if (!displaying.containsKey(player.getUniqueId())) {
                FrozenBossBarHandler.sendSpawnPacket(player, message, health);
            } else {
                FrozenBossBarHandler.sendUpdatePacket(player, message, health);
            }
            displaying.get(player.getUniqueId()).message = message;
            displaying.get(player.getUniqueId()).health = health;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeBossBar(Player player) {
        if (!displaying.containsKey(player.getUniqueId())) {
            return;
        }
        int entityId = displaying.get(player.getUniqueId()).entityId;
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutEntityDestroy(new int[]{entityId}));
        displaying.remove(player.getUniqueId());
        lastUpdatedPosition.remove(player.getUniqueId());
    }

    private static void sendSpawnPacket(Player bukkitPlayer, String message, float health) throws Exception {
        EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        int version = player.playerConnection.networkManager.getVersion();
        displaying.put(bukkitPlayer.getUniqueId(), new BarData(EntityUtils.getFakeEntityId(), message, health));
        BarData stored = displaying.get(bukkitPlayer.getUniqueId());
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        spawnPacketAField.set((Object)packet, stored.entityId);
        DataWatcher watcher = new DataWatcher((Entity)null);
        if (version != 47) {
            spawnPacketBField.set((Object)packet, (byte)EntityType.ENDER_DRAGON.getTypeId());
            watcher.a(6, (Object)Float.valueOf(health * 200.0f));
            spawnPacketCField.set((Object)packet, (int)(player.locX * 32.0));
            spawnPacketDField.set((Object)packet, -6400);
            spawnPacketEField.set((Object)packet, (int)(player.locZ * 32.0));
        } else {
            spawnPacketBField.set((Object)packet, (byte)EntityType.WITHER.getTypeId());
            watcher.a(6, (Object)Float.valueOf(health * 300.0f));
            watcher.a(20, (Object)880);
            double pitch = Math.toRadians(player.pitch);
            double yaw = Math.toRadians(player.yaw);
            spawnPacketCField.set((Object)packet, (int)((player.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0) * 32.0));
            spawnPacketDField.set((Object)packet, (int)((player.locY - Math.sin(pitch) * 32.0) * 32.0));
            spawnPacketEField.set((Object)packet, (int)((player.locZ + Math.sin(yaw) * Math.cos(pitch) * 32.0) * 32.0));
        }
        watcher.a(version != 47 ? 10 : 2, (Object)message);
        spawnPacketLField.set((Object)packet, (Object)watcher);
        player.playerConnection.sendPacket((Packet)packet);
    }

    private static void sendUpdatePacket(Player bukkitPlayer, String message, float health) throws IllegalAccessException {
        EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        int version = player.playerConnection.networkManager.getVersion();
        BarData stored = displaying.get(bukkitPlayer.getUniqueId());
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata();
        metadataPacketAField.set((Object)packet, stored.entityId);
        ArrayList<WatchableObject> objects = new ArrayList<WatchableObject>();
        if (health != stored.health) {
            if (version != 47) {
                objects.add(FrozenBossBarHandler.createWatchableObject(6, Float.valueOf(health * 200.0f)));
            } else {
                objects.add(FrozenBossBarHandler.createWatchableObject(6, Float.valueOf(health * 300.0f)));
            }
        }
        if (!message.equals(stored.message)) {
            objects.add(FrozenBossBarHandler.createWatchableObject(version != 47 ? 10 : 2, message));
        }
        metadataPacketBField.set((Object)packet, objects);
        player.playerConnection.sendPacket((Packet)packet);
    }

    private static WatchableObject createWatchableObject(int id, Object object) {
        return new WatchableObject(classToIdMap.get(object.getClass()), id, object);
    }

    private static void updatePosition(Player bukkitPlayer) {
        int z;
        int y;
        int x;
        if (!displaying.containsKey(bukkitPlayer.getUniqueId())) {
            return;
        }
        EntityPlayer player = ((CraftPlayer)bukkitPlayer).getHandle();
        int version = player.playerConnection.networkManager.getVersion();
        if (version != 47) {
            x = (int)(player.locX * 32.0);
            y = -6400;
            z = (int)(player.locZ * 32.0);
        } else {
            double pitch = Math.toRadians(player.pitch);
            double yaw = Math.toRadians(player.yaw);
            x = (int)((player.locX - Math.sin(yaw) * Math.cos(pitch) * 32.0) * 32.0);
            y = (int)((player.locY - Math.sin(pitch) * 32.0) * 32.0);
            z = (int)((player.locZ + Math.cos(yaw) * Math.cos(pitch) * 32.0) * 32.0);
        }
        player.playerConnection.sendPacket((Packet)new PacketPlayOutEntityTeleport(displaying.get(bukkitPlayer.getUniqueId()).entityId, x, y, z, 0, 0));
    }

    static {
        try {
            spawnPacketAField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("a");
            spawnPacketAField.setAccessible(true);
            spawnPacketBField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("b");
            spawnPacketBField.setAccessible(true);
            spawnPacketCField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("c");
            spawnPacketCField.setAccessible(true);
            spawnPacketDField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("d");
            spawnPacketDField.setAccessible(true);
            spawnPacketEField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("e");
            spawnPacketEField.setAccessible(true);
            spawnPacketLField = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
            spawnPacketLField.setAccessible(true);
            metadataPacketAField = PacketPlayOutEntityMetadata.class.getDeclaredField("a");
            metadataPacketAField.setAccessible(true);
            metadataPacketBField = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
            metadataPacketBField.setAccessible(true);
            Field dataWatcherClassToIdField = DataWatcher.class.getDeclaredField("classToId");
            dataWatcherClassToIdField.setAccessible(true);
            classToIdMap = (TObjectIntHashMap)dataWatcherClassToIdField.get(null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class BarData {
        private final int entityId;
        private String message;
        private float health;

        @ConstructorProperties(value={"entityId", "message", "health"})
        public BarData(int entityId, String message, float health) {
            this.entityId = entityId;
            this.message = message;
            this.health = health;
        }
    }
}

