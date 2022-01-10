/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.qlib.hologram;

import java.util.LinkedHashSet;
import java.util.Set;
import net.frozenorb.qlib.hologram.Hologram;

public final class HologramRegistry {
    private static final Set<Hologram> holograms = new LinkedHashSet<Hologram>();

    public static Set<Hologram> getHolograms() {
        return holograms;
    }
}

