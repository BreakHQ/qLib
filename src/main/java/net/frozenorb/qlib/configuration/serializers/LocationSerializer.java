/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 */
package net.frozenorb.qlib.configuration.serializers;

import net.frozenorb.qlib.configuration.AbstractSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer
extends AbstractSerializer<Location> {
    @Override
    public String toString(Location data) {
        return data.getWorld().getName() + "|" + data.getBlockX() + "|" + data.getBlockY() + "|" + data.getBlockZ();
    }

    @Override
    public Location fromString(String data) {
        String[] parts = data.split("\\|");
        return new Location(Bukkit.getWorld((String)parts[0]), (double)Integer.valueOf(parts[1]).intValue(), (double)Integer.valueOf(parts[2]).intValue(), (double)Integer.valueOf(parts[3]).intValue());
    }
}

