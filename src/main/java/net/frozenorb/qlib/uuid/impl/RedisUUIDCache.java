/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package net.frozenorb.qlib.uuid.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.redis.RedisCommand;
import net.frozenorb.qlib.uuid.UUIDCache;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

public final class RedisUUIDCache
implements UUIDCache {
    private static Map<UUID, String> uuidToName = new ConcurrentHashMap<UUID, String>();
    private static Map<String, UUID> nameToUuid = new ConcurrentHashMap<String, UUID>();

    public RedisUUIDCache() {
        qLib.getInstance().runBackboneRedisCommand(new RedisCommand<Object>(){

            @Override
            public Object execute(Jedis redis) {
                Map<String, String> cache = redis.hgetAll("UUIDCache");
                for (Map.Entry<String, String> cacheEntry : cache.entrySet()) {
                    UUID uuid = UUID.fromString(cacheEntry.getKey());
                    String name = cacheEntry.getValue();
                    uuidToName.put(uuid, name);
                    nameToUuid.put(name.toLowerCase(), uuid);
                }
                return null;
            }
        });
    }

    @Override
    public UUID uuid(String name) {
        return nameToUuid.get(name.toLowerCase());
    }

    @Override
    public String name(UUID uuid) {
        return uuidToName.get(uuid);
    }

    @Override
    public void ensure(UUID uuid) {
        if (String.valueOf(this.name(uuid)).equals("null")) {
            qLib.getInstance().getLogger().warning(uuid + " didn't have a cached name.");
        }
    }

    @Override
    public void update(final UUID uuid, final String name) {
        uuidToName.put(uuid, name);
        for (Map.Entry<String, UUID> entry : new HashMap<String, UUID>(nameToUuid).entrySet()) {
            if (!entry.getValue().equals(uuid)) continue;
            nameToUuid.remove(entry.getKey());
        }
        nameToUuid.put(name.toLowerCase(), uuid);
        new BukkitRunnable(){

            public void run() {
                qLib.getInstance().runBackboneRedisCommand(new RedisCommand<Object>(){

                    @Override
                    public Object execute(Jedis redis) {
                        redis.hset("UUIDCache", uuid.toString(), name);
                        return null;
                    }
                });
            }
        }.runTaskAsynchronously((Plugin)qLib.getInstance());
    }
}

