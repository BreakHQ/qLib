/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package net.frozenorb.qlib.deathmessage;

import java.util.UUID;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;

public interface DeathMessageConfiguration {
    public static final DeathMessageConfiguration DEFAULT_CONFIGURATION = new DeathMessageConfiguration(){

        @Override
        public boolean shouldShowDeathMessage(UUID checkFor, UUID died, UUID killer) {
            return true;
        }

        @Override
        public String formatPlayerName(UUID player) {
            return (Object)ChatColor.RED + UUIDUtils.name(player);
        }
    };

    public boolean shouldShowDeathMessage(UUID var1, UUID var2, UUID var3);

    public String formatPlayerName(UUID var1);

    default public String formatPlayerName(UUID player, UUID formatFor) {
        return this.formatPlayerName(player);
    }

    default public boolean hideWeapons() {
        return false;
    }
}

