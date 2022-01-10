/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketAdapter
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  net.minecraft.server.v1_7_R4.EntityPlayer
 *  net.minecraft.server.v1_7_R4.EntityTrackerEntry
 *  net.minecraft.server.v1_7_R4.Packet
 *  net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn
 *  net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo
 *  net.minecraft.server.v1_7_R4.WorldServer
 *  net.minecraft.util.com.mojang.authlib.GameProfile
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.spigotmc.SpigotConfig
 */
package net.frozenorb.qlib.tab;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.lang.reflect.Field;
import java.util.UUID;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.tab.FrozenTab;
import net.frozenorb.qlib.tab.FrozenTabHandler;
import net.frozenorb.qlib.tab.TabUtils;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.EntityTrackerEntry;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;

public class TabAdapter
extends PacketAdapter {
    private static Field playerField;
    private static Field namedEntitySpawnField;

    public TabAdapter() {
        super((Plugin)qLib.getInstance(), new PacketType[]{PacketType.Play.Server.PLAYER_INFO, PacketType.Play.Server.NAMED_ENTITY_SPAWN});
    }

    public void onPacketSending(PacketEvent event) {
        if (FrozenTabHandler.getLayoutProvider() == null || !this.shouldForbid(event.getPlayer())) {
            return;
        }
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
            PacketContainer packetContainer = event.getPacket();
            String name = (String)packetContainer.getStrings().read(0);
            boolean isOurs = ((String)packetContainer.getStrings().read(0)).startsWith("$");
            int action = (Integer)packetContainer.getIntegers().read(1);
            if (!isOurs && !SpigotConfig.onlyCustomTab) {
                if (action != 4 && this.shouldCancel(event.getPlayer(), event.getPacket())) {
                    event.setCancelled(true);
                }
            } else {
                packetContainer.getStrings().write(0, (Object)name.replace("$", ""));
            }
        } else if (event.getPacketType() == PacketType.Play.Server.NAMED_ENTITY_SPAWN && TabUtils.is18(event.getPlayer()) && !SpigotConfig.onlyCustomTab && Bukkit.getPluginManager().getPlugin("UHC") == null) {
            GameProfile gameProfile;
            PacketPlayOutNamedEntitySpawn packet = (PacketPlayOutNamedEntitySpawn)event.getPacket().getHandle();
            try {
                gameProfile = (GameProfile)namedEntitySpawnField.get((Object)packet);
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
            Bukkit.getScheduler().runTask((Plugin)qLib.getInstance(), () -> {
                Player bukkitPlayer = Bukkit.getPlayer((UUID)gameProfile.getId());
                if (bukkitPlayer == null) {
                    return;
                }
                ((CraftPlayer)event.getPlayer()).getHandle().playerConnection.sendPacket((Packet)PacketPlayOutPlayerInfo.removePlayer((EntityPlayer)((CraftPlayer)bukkitPlayer).getHandle()));
            });
        }
    }

    private boolean shouldCancel(Player player, PacketContainer packetContainer) {
        UUID tabPacketPlayer;
        if (!TabUtils.is18(player)) {
            return true;
        }
        PacketPlayOutPlayerInfo playerInfoPacket = (PacketPlayOutPlayerInfo)packetContainer.getHandle();
        EntityPlayer recipient = ((CraftPlayer)player).getHandle();
        try {
            tabPacketPlayer = ((GameProfile)playerField.get((Object)playerInfoPacket)).getId();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Player bukkitPlayer = Bukkit.getPlayer((UUID)tabPacketPlayer);
        if (bukkitPlayer == null) {
            return true;
        }
        EntityTrackerEntry trackerEntry = (EntityTrackerEntry)((WorldServer)((CraftPlayer)bukkitPlayer).getHandle().getWorld()).getTracker().trackedEntities.get(bukkitPlayer.getEntityId());
        if (trackerEntry == null) {
            return true;
        }
        return !trackerEntry.trackedPlayers.contains((Object)recipient);
    }

    private boolean shouldForbid(Player player) {
        String playerName = player.getName();
        FrozenTab playerTab = FrozenTabHandler.getTabs().get(playerName);
        return playerTab != null && playerTab.isInitiated();
    }

    static {
        try {
            playerField = PacketPlayOutPlayerInfo.class.getDeclaredField("player");
            playerField.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            namedEntitySpawnField = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("b");
            namedEntitySpawnField.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

