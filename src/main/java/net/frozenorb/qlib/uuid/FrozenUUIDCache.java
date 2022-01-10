/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.uuid;

import com.google.common.base.Preconditions;
import java.util.UUID;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.uuid.UUIDCache;
import net.frozenorb.qlib.uuid.UUIDListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class FrozenUUIDCache {
    private static UUIDCache impl = null;
    private static boolean initiated = false;

    private FrozenUUIDCache() {
    }

    public static void init() {
        Preconditions.checkState((!initiated ? 1 : 0) != 0);
        initiated = true;
        try {
            impl = (UUIDCache)Class.forName(qLib.getInstance().getConfig().getString("UUIDCache.Backend", "net.frozenorb.qlib.uuid.impl.RedisUUIDCache")).newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        qLib.getInstance().getServer().getPluginManager().registerEvents((Listener)new UUIDListener(), (Plugin)qLib.getInstance());
    }

    public static UUID uuid(String name) {
        return impl.uuid(name);
    }

    public static String name(UUID uuid) {
        return impl.name(uuid);
    }

    public static void ensure(UUID uuid) {
        impl.ensure(uuid);
    }

    public static void update(UUID uuid, String name) {
        impl.update(uuid, name);
    }

    public static UUIDCache getImpl() {
        return impl;
    }
}

