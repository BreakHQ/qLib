/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_7_R4.EntityPlayer
 *  net.minecraft.server.v1_7_R4.MinecraftServer
 *  net.minecraft.server.v1_7_R4.PlayerInteractManager
 *  net.minecraft.server.v1_7_R4.World
 *  net.minecraft.util.com.mojang.authlib.GameProfile
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.v1_7_R4.CraftServer
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.qlib.command.parameter.offlineplayer;

import java.util.UUID;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.util.Callback;
import net.frozenorb.qlib.util.UUIDUtils;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.World;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class OfflinePlayerWrapper {
    private String source;
    private UUID uniqueId;
    private String name;

    public OfflinePlayerWrapper(String source) {
        this.source = source;
    }

    public void loadAsync(final Callback<Player> callback) {
        new BukkitRunnable(){

            public void run() {
                final Player player = OfflinePlayerWrapper.this.loadSync();
                new BukkitRunnable(){

                    public void run() {
                        callback.callback(player);
                    }
                }.runTask((Plugin)qLib.getInstance());
            }
        }.runTaskAsynchronously((Plugin)qLib.getInstance());
    }

    public Player loadSync() {
        if (!(this.source.charAt(0) != '\"' && this.source.charAt(0) != '\'' || this.source.charAt(this.source.length() - 1) != '\"' && this.source.charAt(this.source.length() - 1) != '\'')) {
            this.source = this.source.replace("'", "").replace("\"", "");
            this.uniqueId = UUIDUtils.uuid(this.source);
            if (this.uniqueId == null) {
                this.name = this.source;
                return null;
            }
            this.name = UUIDUtils.name(this.uniqueId);
            if (Bukkit.getPlayer((UUID)this.uniqueId) != null) {
                return Bukkit.getPlayer((UUID)this.uniqueId);
            }
            if (!Bukkit.getOfflinePlayer((UUID)this.uniqueId).hasPlayedBefore()) {
                return null;
            }
            MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
            EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager((World)server.getWorldServer(0)));
            CraftPlayer player = entity.getBukkitEntity();
            if (player != null) {
                player.loadData();
            }
            return player;
        }
        if (Bukkit.getPlayer((String)this.source) != null) {
            return Bukkit.getPlayer((String)this.source);
        }
        this.uniqueId = UUIDUtils.uuid(this.source);
        if (this.uniqueId == null) {
            this.name = this.source;
            return null;
        }
        this.name = UUIDUtils.name(this.uniqueId);
        if (Bukkit.getPlayer((UUID)this.uniqueId) != null) {
            return Bukkit.getPlayer((UUID)this.uniqueId);
        }
        if (!Bukkit.getOfflinePlayer((UUID)this.uniqueId).hasPlayedBefore()) {
            return null;
        }
        MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
        EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(this.uniqueId, this.name), new PlayerInteractManager((World)server.getWorldServer(0)));
        CraftPlayer player = entity.getBukkitEntity();
        if (player != null) {
            player.loadData();
        }
        return player;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getName() {
        return this.name;
    }
}

