/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.util.com.google.common.collect.ImmutableList
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 */
package net.frozenorb.qlib.deathmessage;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.qlib.deathmessage.DeathMessageConfiguration;
import net.frozenorb.qlib.deathmessage.damage.Damage;
import net.frozenorb.qlib.deathmessage.listener.DamageListener;
import net.frozenorb.qlib.deathmessage.listener.DeathListener;
import net.frozenorb.qlib.deathmessage.listener.DisconnectListener;
import net.frozenorb.qlib.deathmessage.tracker.ArrowTracker;
import net.frozenorb.qlib.deathmessage.tracker.BurnTracker;
import net.frozenorb.qlib.deathmessage.tracker.EntityTracker;
import net.frozenorb.qlib.deathmessage.tracker.FallTracker;
import net.frozenorb.qlib.deathmessage.tracker.GeneralTracker;
import net.frozenorb.qlib.deathmessage.tracker.PvPTracker;
import net.frozenorb.qlib.deathmessage.tracker.VoidTracker;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class FrozenDeathMessageHandler {
    private static DeathMessageConfiguration configuration = DeathMessageConfiguration.DEFAULT_CONFIGURATION;
    private static Map<UUID, List<Damage>> damage = new HashMap<UUID, List<Damage>>();
    private static boolean initiated = false;

    private FrozenDeathMessageHandler() {
    }

    public static void init() {
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated = true;
        PluginManager pluginManager = qLib.getInstance().getServer().getPluginManager();
        pluginManager.registerEvents((Listener)new DamageListener(), (Plugin)qLib.getInstance());
        pluginManager.registerEvents((Listener)new DeathListener(), (Plugin)qLib.getInstance());
        pluginManager.registerEvents((Listener)new DisconnectListener(), (Plugin)qLib.getInstance());
        pluginManager.registerEvents((Listener)new GeneralTracker(), (Plugin)qLib.getInstance());
        pluginManager.registerEvents((Listener)new PvPTracker(), (Plugin)qLib.getInstance());
        pluginManager.registerEvents((Listener)new EntityTracker(), (Plugin)qLib.getInstance());
        pluginManager.registerEvents((Listener)new FallTracker(), (Plugin)qLib.getInstance());
        pluginManager.registerEvents((Listener)new ArrowTracker(), (Plugin)qLib.getInstance());
        pluginManager.registerEvents((Listener)new VoidTracker(), (Plugin)qLib.getInstance());
        pluginManager.registerEvents((Listener)new BurnTracker(), (Plugin)qLib.getInstance());
    }

    public static List<Damage> getDamage(Player player) {
        return damage.containsKey(player.getUniqueId()) ? damage.get(player.getUniqueId()) : ImmutableList.of();
    }

    public static void addDamage(Player player, Damage addedDamage) {
        damage.putIfAbsent(player.getUniqueId(), new ArrayList());
        List<Damage> damageList = damage.get(player.getUniqueId());
        while (damageList.size() > 30) {
            damageList.remove(0);
        }
        damageList.add(addedDamage);
    }

    public static void clearDamage(Player player) {
        damage.remove(player.getUniqueId());
    }

    public static DeathMessageConfiguration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(DeathMessageConfiguration configuration) {
        FrozenDeathMessageHandler.configuration = configuration;
    }
}

