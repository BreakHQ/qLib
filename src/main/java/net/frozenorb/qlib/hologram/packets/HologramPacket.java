/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.PacketContainer
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.hologram.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.bukkit.entity.Player;

public class HologramPacket {
    private List<PacketContainer> packets;
    private List<Integer> entityIds;

    public HologramPacket(List<PacketContainer> packets, List<Integer> entityIds) {
        this.packets = packets;
        this.entityIds = entityIds;
    }

    public void sendToPlayer(Player player) {
        for (PacketContainer packetContainer : this.packets) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Integer> getEntityIds() {
        return this.entityIds;
    }

    public List<PacketContainer> getPackets() {
        return this.packets;
    }
}

