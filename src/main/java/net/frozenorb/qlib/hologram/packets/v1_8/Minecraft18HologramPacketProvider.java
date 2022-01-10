/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.reflect.StructureModifier
 *  com.comphenix.protocol.wrappers.WrappedDataWatcher
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 */
package net.frozenorb.qlib.hologram.packets.v1_8;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.frozenorb.qlib.hologram.HologramLine;
import net.frozenorb.qlib.hologram.packets.HologramPacket;
import net.frozenorb.qlib.hologram.packets.HologramPacketProvider;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Minecraft18HologramPacketProvider
implements HologramPacketProvider {
    @Override
    public HologramPacket getPacketsFor(Location location, HologramLine line) {
        List<PacketContainer> packets = Collections.singletonList(this.createArmorStandPacket(line.getSkullId(), line.getText(), location));
        return new HologramPacket(packets, Arrays.asList(line.getSkullId(), -1337));
    }

    protected PacketContainer createArmorStandPacket(int witherSkullId, String text, Location location) {
        PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        StructureModifier ints = displayPacket.getIntegers();
        ints.write(0, (Object)witherSkullId);
        ints.write(1, (Object)30);
        ints.write(2, (Object)((int)(location.getX() * 32.0)));
        ints.write(3, (Object)((int)((location.getY() - 2.0) * 32.0)));
        ints.write(4, (Object)((int)(location.getZ() * 32.0)));
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (Object)32);
        watcher.setObject(2, (Object)ChatColor.translateAlternateColorCodes((char)'&', (String)text));
        watcher.setObject(3, (Object)1);
        displayPacket.getDataWatcherModifier().write(0, (Object)watcher);
        return displayPacket;
    }
}

