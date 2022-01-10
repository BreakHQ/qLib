/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.border;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.qlib.border.Border;
import net.frozenorb.qlib.border.BorderListener;
import net.frozenorb.qlib.border.EnsureInsideRunnable;
import net.frozenorb.qlib.border.InternalBorderListener;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class FrozenBorderHandler {
    private static final Map<World, Border> borderMap = new HashMap<World, Border>();
    private static boolean initiated = false;

    private FrozenBorderHandler() {
    }

    public static void init() {
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated = true;
        Bukkit.getPluginManager().registerEvents((Listener)new BorderListener(), (Plugin)qLib.getInstance());
        Bukkit.getPluginManager().registerEvents((Listener)new InternalBorderListener(), (Plugin)qLib.getInstance());
        new EnsureInsideRunnable().runTaskTimer((Plugin)qLib.getInstance(), 5L, 5L);
    }

    public static Border getBorderForWorld(World world) {
        return borderMap.get((Object)world);
    }

    static void addBorder(Border border) {
        borderMap.put(border.getOrigin().getWorld(), border);
    }
}

