/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.reflect.StructureModifier
 *  com.comphenix.protocol.wrappers.WrappedDataWatcher
 *  net.minecraft.server.v1_7_R4.MathHelper
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 */
package net.frozenorb.qlib.hologram.packets.v1_7;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.util.Arrays;
import net.frozenorb.qlib.hologram.HologramLine;
import net.frozenorb.qlib.hologram.packets.HologramPacket;
import net.frozenorb.qlib.hologram.packets.HologramPacketProvider;
import net.minecraft.server.v1_7_R4.MathHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Minecraft17HologramPacketProvider
implements HologramPacketProvider {
    @Override
    public HologramPacket getPacketsFor(Location location, HologramLine line) {
        PacketContainer skullPacket = this.createWitherSkull(location, line.getSkullId());
        PacketContainer horsePacket = this.createHorse(location, line.getHorseId(), line.getText());
        PacketContainer attachPacket = new PacketContainer(PacketType.Play.Server.ATTACH_ENTITY);
        attachPacket.getIntegers().write(1, (Object)line.getHorseId());
        attachPacket.getIntegers().write(2, (Object)line.getSkullId());
        return new HologramPacket(Arrays.asList(new PacketContainer[]{skullPacket, horsePacket, attachPacket}), Arrays.asList(line.getSkullId(), line.getHorseId()));
    }

    protected PacketContainer createWitherSkull(Location location, int id) {
        PacketContainer skull = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        StructureModifier ints = skull.getIntegers();
        ints.write(0, (Object)id);
        ints.write(1, (Object)((int)(location.getX() * 32.0)));
        ints.write(2, (Object)MathHelper.floor((double)((location.getY() - 0.13 + 55.0) * 32.0)));
        ints.write(3, (Object)((int)(location.getZ() * 32.0)));
        ints.write(9, (Object)66);
        return skull;
    }

    protected PacketContainer createHorse(Location location, int id, String text) {
        PacketContainer horse = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        horse.getIntegers().write(0, (Object)id);
        horse.getIntegers().write(1, (Object)100);
        horse.getIntegers().write(2, (Object)((int)(location.getX() * 32.0)));
        horse.getIntegers().write(3, (Object)MathHelper.floor((double)((location.getY() + 55.0) * 32.0)));
        horse.getIntegers().write(4, (Object)((int)(location.getZ() * 32.0)));
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (Object)0);
        watcher.setObject(1, (Object)300);
        watcher.setObject(10, (Object)ChatColor.translateAlternateColorCodes((char)'&', (String)text));
        watcher.setObject(11, (Object)1);
        watcher.setObject(12, (Object)-1700000);
        horse.getDataWatcherModifier().write(0, (Object)watcher);
        return horse;
    }
}

