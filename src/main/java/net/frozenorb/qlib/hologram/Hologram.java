/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 */
package net.frozenorb.qlib.hologram;

import java.util.Collection;
import java.util.List;
import org.bukkit.Location;

public interface Hologram {
    public void send();

    public void destroy();

    public void addLines(String ... var1);

    public void setLine(int var1, String var2);

    public void setLines(Collection<String> var1);

    public List<String> getLines();

    public Location getLocation();
}

