/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.xpacket;

import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.xpacket.XPacket;
import net.frozenorb.qlib.xpacket.XPacketPubSub;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class FrozenXPacketHandler {
    private static final String GLOBAL_MESSAGE_CHANNEL = "XPacket:All";
    static final String PACKET_MESSAGE_DIVIDER = "||";

    public static void init() {
        FileConfiguration config = qLib.getInstance().getConfig();
        String localHost = config.getString("Redis.Host");
        int localDb = config.getInt("Redis.DbId", 0);
        String remoteHost = config.getString("BackboneRedis.Host");
        int remoteDb = config.getInt("BackboneRedis.DbId", 0);
        boolean sameServer = localHost.equalsIgnoreCase(remoteHost) && localDb == remoteDb;
        FrozenXPacketHandler.connectToServer(qLib.getInstance().getLocalJedisPool());
        if (!sameServer) {
            FrozenXPacketHandler.connectToServer(qLib.getInstance().getBackboneJedisPool());
        }
    }

    public static void connectToServer(JedisPool connectTo) {
        if (qLib.testing) {
            return;
        }
        Thread subscribeThread = new Thread(() -> {
            while (qLib.getInstance().isEnabled()) {
                try {
                    Jedis jedis = connectTo.getResource();
                    Throwable throwable = null;
                    try {
                        XPacketPubSub pubSub = new XPacketPubSub();
                        String channel = GLOBAL_MESSAGE_CHANNEL;
                        jedis.subscribe(pubSub, channel);
                    }
                    catch (Throwable throwable2) {
                        throwable = throwable2;
                        throw throwable2;
                    }
                    finally {
                        if (jedis == null) continue;
                        if (throwable != null) {
                            try {
                                jedis.close();
                            }
                            catch (Throwable throwable3) {
                                throwable.addSuppressed(throwable3);
                            }
                            continue;
                        }
                        jedis.close();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "qLib - XPacket Subscribe Thread");
        subscribeThread.setDaemon(true);
        subscribeThread.start();
    }

    public static void sendToAll(XPacket packet) {
        FrozenXPacketHandler.send(packet, qLib.getInstance().getBackboneJedisPool());
    }

    public static void sendToAllViaLocal(XPacket packet) {
        FrozenXPacketHandler.send(packet, qLib.getInstance().getLocalJedisPool());
    }

    public static void send(XPacket packet, JedisPool sendOn) {
        if (!qLib.getInstance().isEnabled()) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)qLib.getInstance(), () -> {
            try (Jedis jedis = sendOn.getResource();){
                String encodedPacket = packet.getClass().getName() + PACKET_MESSAGE_DIVIDER + qLib.PLAIN_GSON.toJson((Object)packet);
                jedis.publish(GLOBAL_MESSAGE_CHANNEL, encodedPacket);
            }
        });
    }

    private FrozenXPacketHandler() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

