/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.hologram;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import net.frozenorb.qlib.hologram.HologramBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class Holograms {
    public static HologramBuilder forPlayer(Player player) {
        return new HologramBuilder(Collections.singleton(player.getUniqueId()));
    }

    public static HologramBuilder forPlayers(Collection<Player> players) {
        if (players == null) {
            return new HologramBuilder(null);
        }
        return new HologramBuilder(players.stream().map(Entity::getUniqueId).collect(Collectors.toSet()));
    }

    public static HologramBuilder newHologram() {
        return Holograms.forPlayers(null);
    }
}

