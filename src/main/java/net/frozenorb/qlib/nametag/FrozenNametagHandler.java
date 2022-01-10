/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.primitives.Ints
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.nametag;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.qlib.nametag.NametagInfo;
import net.frozenorb.qlib.nametag.NametagListener;
import net.frozenorb.qlib.nametag.NametagProvider;
import net.frozenorb.qlib.nametag.NametagThread;
import net.frozenorb.qlib.nametag.NametagUpdate;
import net.frozenorb.qlib.packet.ScoreboardTeamPacketMod;
import net.frozenorb.qlib.qLib;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class FrozenNametagHandler {
    private static Map<String, Map<String, NametagInfo>> teamMap = new ConcurrentHashMap<String, Map<String, NametagInfo>>();
    private static List<NametagInfo> registeredTeams = Collections.synchronizedList(new ArrayList());
    private static int teamCreateIndex = 1;
    private static List<NametagProvider> providers = new ArrayList<NametagProvider>();
    private static boolean nametagRestrictionEnabled = false;
    private static String nametagRestrictBypass = "";
    private static boolean initiated = false;
    private static boolean async = true;
    private static int updateInterval = 2;

    private FrozenNametagHandler() {
    }

    public static void init() {
        if (qLib.getInstance().getConfig().getBoolean("disableNametags", false)) {
            return;
        }
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated = true;
        nametagRestrictionEnabled = qLib.getInstance().getConfig().getBoolean("NametagPacketRestriction.Enabled", false);
        nametagRestrictBypass = qLib.getInstance().getConfig().getString("NametagPacketRestriction.BypassPrefix").replace("&", "\u00a7");
        new NametagThread().start();
        qLib.getInstance().getServer().getPluginManager().registerEvents((Listener)new NametagListener(), (Plugin)qLib.getInstance());
        FrozenNametagHandler.registerProvider(new NametagProvider.DefaultNametagProvider());
    }

    public static void registerProvider(NametagProvider newProvider) {
        providers.add(newProvider);
        Collections.sort(providers, new Comparator<NametagProvider>(){

            @Override
            public int compare(NametagProvider a, NametagProvider b) {
                return Ints.compare((int)b.getWeight(), (int)a.getWeight());
            }
        });
    }

    public static void reloadPlayer(Player toRefresh) {
        NametagUpdate update = new NametagUpdate(toRefresh);
        if (async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            FrozenNametagHandler.applyUpdate(update);
        }
    }

    public static void reloadOthersFor(Player refreshFor) {
        for (Player toRefresh : qLib.getInstance().getServer().getOnlinePlayers()) {
            if (refreshFor == toRefresh) continue;
            FrozenNametagHandler.reloadPlayer(toRefresh, refreshFor);
        }
    }

    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        NametagUpdate update = new NametagUpdate(toRefresh, refreshFor);
        if (async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            FrozenNametagHandler.applyUpdate(update);
        }
    }

    protected static void applyUpdate(NametagUpdate nametagUpdate) {
        Player toRefreshPlayer = qLib.getInstance().getServer().getPlayerExact(nametagUpdate.getToRefresh());
        if (toRefreshPlayer == null) {
            return;
        }
        if (nametagUpdate.getRefreshFor() == null) {
            for (Player refreshFor : qLib.getInstance().getServer().getOnlinePlayers()) {
                FrozenNametagHandler.reloadPlayerInternal(toRefreshPlayer, refreshFor);
            }
        } else {
            Player refreshForPlayer = qLib.getInstance().getServer().getPlayerExact(nametagUpdate.getRefreshFor());
            if (refreshForPlayer != null) {
                FrozenNametagHandler.reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
            }
        }
    }

    protected static void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        String prefix;
        if (!refreshFor.hasMetadata("qLibNametag-LoggedIn")) {
            return;
        }
        NametagInfo provided = null;
        int providerIndex = 0;
        while (provided == null) {
            provided = providers.get(providerIndex++).fetchNametag(toRefresh, refreshFor);
        }
        if (((CraftPlayer)refreshFor).getHandle().playerConnection.networkManager.getVersion() > 5 && nametagRestrictionEnabled && (prefix = provided.getPrefix()) != null && !prefix.equalsIgnoreCase(nametagRestrictBypass)) {
            return;
        }
        Map<Object, Object> teamInfoMap = new HashMap();
        if (teamMap.containsKey(refreshFor.getName())) {
            teamInfoMap = teamMap.get(refreshFor.getName());
        }
        new ScoreboardTeamPacketMod(provided.getName(), Arrays.asList(toRefresh.getName()), 3).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefresh.getName(), provided);
        teamMap.put(refreshFor.getName(), teamInfoMap);
    }

    protected static void initiatePlayer(Player player) {
        for (NametagInfo teamInfo : registeredTeams) {
            teamInfo.getTeamAddPacket().sendToPlayer(player);
        }
    }

    protected static NametagInfo getOrCreate(String prefix, String suffix) {
        for (NametagInfo teamInfo : registeredTeams) {
            if (!teamInfo.getPrefix().equals(prefix) || !teamInfo.getSuffix().equals(suffix)) continue;
            return teamInfo;
        }
        NametagInfo newTeam = new NametagInfo(String.valueOf(teamCreateIndex++), prefix, suffix);
        registeredTeams.add(newTeam);
        ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();
        for (Player player : qLib.getInstance().getServer().getOnlinePlayers()) {
            addPacket.sendToPlayer(player);
        }
        return newTeam;
    }

    protected static Map<String, Map<String, NametagInfo>> getTeamMap() {
        return teamMap;
    }

    public static boolean isNametagRestrictionEnabled() {
        return nametagRestrictionEnabled;
    }

    public static void setNametagRestrictionEnabled(boolean nametagRestrictionEnabled) {
        FrozenNametagHandler.nametagRestrictionEnabled = nametagRestrictionEnabled;
    }

    public static String getNametagRestrictBypass() {
        return nametagRestrictBypass;
    }

    public static void setNametagRestrictBypass(String nametagRestrictBypass) {
        FrozenNametagHandler.nametagRestrictBypass = nametagRestrictBypass;
    }

    public static boolean isInitiated() {
        return initiated;
    }

    public static boolean isAsync() {
        return async;
    }

    public static void setAsync(boolean async) {
        FrozenNametagHandler.async = async;
    }

    public static int getUpdateInterval() {
        return updateInterval;
    }

    public static void setUpdateInterval(int updateInterval) {
        FrozenNametagHandler.updateInterval = updateInterval;
    }
}

