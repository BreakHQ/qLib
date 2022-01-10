/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketAdapter
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.wrappers.EnumWrappers$ClientCommand
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 */
package net.frozenorb.qlib.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.frozenorb.qlib.protocol.event.PlayerCloseInventoryEvent;
import net.frozenorb.qlib.protocol.event.PlayerOpenInventoryEvent;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public class InventoryAdapter
extends PacketAdapter {
    private static Set<UUID> currentlyOpen = new HashSet<UUID>();

    public InventoryAdapter() {
        super((Plugin)qLib.getInstance(), new PacketType[]{PacketType.Play.Client.CLIENT_COMMAND, PacketType.Play.Client.CLOSE_WINDOW});
    }

    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        PacketContainer packet = event.getPacket();
        if (packet.getType() == PacketType.Play.Client.CLIENT_COMMAND && packet.getClientCommands().size() != 0 && packet.getClientCommands().read(0) == EnumWrappers.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            if (!currentlyOpen.contains(player.getUniqueId())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)qLib.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)new PlayerOpenInventoryEvent(player)));
            }
            currentlyOpen.add(player.getUniqueId());
        } else if (packet.getType() == PacketType.Play.Client.CLOSE_WINDOW) {
            if (currentlyOpen.contains(player.getUniqueId())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)qLib.getInstance(), () -> Bukkit.getPluginManager().callEvent((Event)new PlayerCloseInventoryEvent(player)));
            }
            currentlyOpen.remove(player.getUniqueId());
        }
    }

    public static Set<UUID> getCurrentlyOpen() {
        return currentlyOpen;
    }
}

