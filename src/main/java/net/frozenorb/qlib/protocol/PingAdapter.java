/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketAdapter
 *  com.comphenix.protocol.events.PacketEvent
 *  com.google.common.collect.Maps
 *  net.minecraft.server.v1_7_R4.MinecraftServer
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Maps;
import java.beans.ConstructorProperties;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.qlib.qLib;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PingAdapter
extends PacketAdapter
implements Listener {
    private static final Map<UUID, PingCallback> callbacks = Maps.newConcurrentMap();
    private static final Map<UUID, Integer> ping = Maps.newConcurrentMap();
    private static final Map<UUID, Integer> lastReply = Maps.newConcurrentMap();

    public PingAdapter() {
        super((Plugin)qLib.getInstance(), new PacketType[]{PacketType.Play.Server.KEEP_ALIVE, PacketType.Play.Client.KEEP_ALIVE});
    }

    public void onPacketSending(final PacketEvent event) {
        int id = (Integer)event.getPacket().getIntegers().read(0);
        callbacks.put(event.getPlayer().getUniqueId(), new PingCallback(id){

            @Override
            public void call() {
                int ping = (int)(System.currentTimeMillis() - this.getSendTime());
                ping.put(event.getPlayer().getUniqueId(), ping);
                lastReply.put(event.getPlayer().getUniqueId(), MinecraftServer.currentTick);
            }
        });
    }

    public void onPacketReceiving(PacketEvent event) {
        Iterator<Map.Entry<UUID, PingCallback>> iterator = callbacks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PingCallback> entry = iterator.next();
            if (entry.getValue().getId() != ((Integer)event.getPacket().getIntegers().read(0)).intValue()) continue;
            entry.getValue().call();
            iterator.remove();
            break;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ping.remove(event.getPlayer().getUniqueId());
        lastReply.remove(event.getPlayer().getUniqueId());
        callbacks.remove(event.getPlayer().getUniqueId());
    }

    public static int getAveragePing() {
        if (ping.size() == 0) {
            return 0;
        }
        int x = 0;
        for (int p : ping.values()) {
            x += p;
        }
        return x / ping.size();
    }

    public static Map<UUID, Integer> getPing() {
        return ping;
    }

    public static Map<UUID, Integer> getLastReply() {
        return lastReply;
    }

    private static abstract class PingCallback {
        private final long sendTime = System.currentTimeMillis();
        private final int id;

        public abstract void call();

        @ConstructorProperties(value={"id"})
        public PingCallback(int id) {
            this.id = id;
        }

        public long getSendTime() {
            return this.sendTime;
        }

        public int getId() {
            return this.id;
        }
    }
}

