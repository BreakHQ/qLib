/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package net.frozenorb.qlib.combatlogger;

import java.util.UUID;
import net.frozenorb.qlib.deathmessage.FrozenDeathMessageHandler;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;

public interface CombatLoggerConfiguration {
    public static final CombatLoggerConfiguration DEFAULT_CONFIGURATION = new CombatLoggerConfiguration(){

        @Override
        public String formatPlayerName(UUID user) {
            if (FrozenDeathMessageHandler.getConfiguration() != null) {
                return FrozenDeathMessageHandler.getConfiguration().formatPlayerName(user) + (Object)ChatColor.GRAY + " (Combat-Logger)";
            }
            return (Object)ChatColor.RED + UUIDUtils.name(user) + (Object)ChatColor.GRAY + " (Combat-Logger)";
        }
    };

    public String formatPlayerName(UUID var1);

    default public String formatPlayerName(UUID user, UUID formatFor) {
        return this.formatPlayerName(user);
    }
}

