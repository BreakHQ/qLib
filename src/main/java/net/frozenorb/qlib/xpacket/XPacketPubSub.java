/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.xpacket;

import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.xpacket.XPacket;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.JedisPubSub;

final class XPacketPubSub
extends JedisPubSub {
    XPacketPubSub() {
    }

    @Override
    public void onMessage(String channel, String message) {
        Class<?> packetClass;
        int packetMessageSplit = message.indexOf("||");
        String packetClassStr = message.substring(0, packetMessageSplit);
        String messageJson = message.substring(packetMessageSplit + "||".length());
        try {
            packetClass = Class.forName(packetClassStr);
        }
        catch (ClassNotFoundException ignored) {
            return;
        }
        XPacket packet = (XPacket)qLib.PLAIN_GSON.fromJson(messageJson, packetClass);
        if (qLib.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTask((Plugin)qLib.getInstance(), packet::onReceive);
        }
    }
}

