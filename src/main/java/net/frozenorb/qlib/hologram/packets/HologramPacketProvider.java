/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 */
package net.frozenorb.qlib.hologram.packets;

import net.frozenorb.qlib.hologram.HologramLine;
import net.frozenorb.qlib.hologram.packets.HologramPacket;
import org.bukkit.Location;

public interface HologramPacketProvider {
    public HologramPacket getPacketsFor(Location var1, HologramLine var2);
}

