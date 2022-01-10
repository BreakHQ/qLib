/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.com.google.common.collect.Multimap
 *  net.minecraft.util.com.mojang.authlib.GameProfile
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.tab;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.tab.FrozenTabHandler;
import net.minecraft.util.com.google.common.collect.Multimap;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class TabUtils {
    private static Map<String, GameProfile> cache = new ConcurrentHashMap<String, GameProfile>();

    public static boolean is18(Player player) {
        return ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() > 20;
    }

    public static GameProfile getOrCreateProfile(String name, UUID id) {
        GameProfile player = cache.get(name);
        if (player == null) {
            player = new GameProfile(id, name);
            player.getProperties().putAll((Multimap)FrozenTabHandler.getDefaultPropertyMap());
            cache.put(name, player);
        }
        return player;
    }

    public static GameProfile getOrCreateProfile(String name) {
        return TabUtils.getOrCreateProfile(name, new UUID(qLib.RANDOM.nextLong(), qLib.RANDOM.nextLong()));
    }
}

