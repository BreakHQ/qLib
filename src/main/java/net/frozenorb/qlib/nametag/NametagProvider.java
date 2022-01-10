/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package net.frozenorb.qlib.nametag;

import java.beans.ConstructorProperties;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.nametag.NametagInfo;
import org.bukkit.entity.Player;

public abstract class NametagProvider {
    private String name;
    private int weight;

    public abstract NametagInfo fetchNametag(Player var1, Player var2);

    public static final NametagInfo createNametag(String prefix, String suffix) {
        return FrozenNametagHandler.getOrCreate(prefix, suffix);
    }

    @ConstructorProperties(value={"name", "weight"})
    public NametagProvider(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return this.name;
    }

    public int getWeight() {
        return this.weight;
    }

    protected static final class DefaultNametagProvider
    extends NametagProvider {
        public DefaultNametagProvider() {
            super("Default Provider", 0);
        }

        @Override
        public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
            return DefaultNametagProvider.createNametag("", "");
        }
    }
}

