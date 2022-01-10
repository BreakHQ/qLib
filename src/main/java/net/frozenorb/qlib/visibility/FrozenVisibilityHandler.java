/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerChatTabCompleteEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.visibility;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.visibility.OverrideAction;
import net.frozenorb.qlib.visibility.OverrideHandler;
import net.frozenorb.qlib.visibility.VisibilityAction;
import net.frozenorb.qlib.visibility.VisibilityHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class FrozenVisibilityHandler {
    private static final Map<String, VisibilityHandler> handlers = new LinkedHashMap<String, VisibilityHandler>();
    private static final Map<String, OverrideHandler> overrideHandlers = new LinkedHashMap<String, OverrideHandler>();
    private static boolean initiated = false;

    private FrozenVisibilityHandler() {
    }

    public static void init() {
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated = true;
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler(priority=EventPriority.LOWEST)
            public void onPlayerJoin(PlayerJoinEvent event) {
                FrozenVisibilityHandler.update(event.getPlayer());
            }

            @EventHandler(priority=EventPriority.LOWEST)
            public void onTabComplete(PlayerChatTabCompleteEvent event) {
                String token = event.getLastToken();
                Collection completions = event.getTabCompletions();
                completions.clear();
                for (Player target : Bukkit.getOnlinePlayers()) {
                    if (!FrozenVisibilityHandler.treatAsOnline(target, event.getPlayer()) || !StringUtils.startsWithIgnoreCase((String)target.getName(), (String)token)) continue;
                    completions.add(target.getName());
                }
            }
        }, (Plugin)qLib.getInstance());
    }

    public static void registerHandler(String identifier, VisibilityHandler handler) {
        handlers.put(identifier, handler);
    }

    public static void registerOverride(String identifier, OverrideHandler handler) {
        overrideHandlers.put(identifier, handler);
    }

    public static void update(Player player) {
        if (handlers.isEmpty() && overrideHandlers.isEmpty()) {
            return;
        }
        FrozenVisibilityHandler.updateAllTo(player);
        FrozenVisibilityHandler.updateToAll(player);
    }

    @Deprecated
    public static void updateAllTo(Player viewer) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!FrozenVisibilityHandler.shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
                continue;
            }
            viewer.showPlayer(target);
        }
    }

    @Deprecated
    public static void updateToAll(Player target) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (!FrozenVisibilityHandler.shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
                continue;
            }
            viewer.showPlayer(target);
        }
    }

    public static boolean treatAsOnline(Player target, Player viewer) {
        return viewer.canSee(target) || !target.hasMetadata("invisible") || viewer.hasPermission("basic.staff");
    }

    private static boolean shouldSee(Player target, Player viewer) {
        for (OverrideHandler overrideHandler : overrideHandlers.values()) {
            if (overrideHandler.getAction(target, viewer) != OverrideAction.SHOW) continue;
            return true;
        }
        for (VisibilityHandler visibilityHandler : handlers.values()) {
            if (visibilityHandler.getAction(target, viewer) != VisibilityAction.HIDE) continue;
            return false;
        }
        return true;
    }

    public static List<String> getDebugInfo(Player target, Player viewer) {
        ChatColor color;
        Enum action;
        Object handler;
        ArrayList<String> debug = new ArrayList<String>();
        Boolean canSee = null;
        for (Map.Entry<String, OverrideHandler> entry : overrideHandlers.entrySet()) {
            handler = entry.getValue();
            action = handler.getAction(target, viewer);
            color = ChatColor.GRAY;
            if (action == OverrideAction.SHOW && canSee == null) {
                canSee = true;
                color = ChatColor.GREEN;
            }
            debug.add((Object)color + "Overriding Handler: \"" + entry.getKey() + "\": " + action);
        }
        for (Map.Entry<String, Object> entry : handlers.entrySet()) {
            handler = (VisibilityHandler)entry.getValue();
            action = handler.getAction(target, viewer);
            color = ChatColor.GRAY;
            if (action == VisibilityAction.HIDE && canSee == null) {
                canSee = false;
                color = ChatColor.GREEN;
            }
            debug.add((Object)color + "Normal Handler: \"" + entry.getKey() + "\": " + action);
        }
        if (canSee == null) {
            canSee = true;
        }
        debug.add((Object)ChatColor.AQUA + "Result: " + viewer.getName() + " " + (canSee != false ? "can" : "cannot") + " see " + target.getName());
        return debug;
    }
}

