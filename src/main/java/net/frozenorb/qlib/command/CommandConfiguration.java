/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package net.frozenorb.qlib.command;

import org.bukkit.ChatColor;

public class CommandConfiguration {
    private String noPermissionMessage;

    public CommandConfiguration setNoPermissionMessage(String noPermissionMessage) {
        this.noPermissionMessage = ChatColor.translateAlternateColorCodes((char)'&', (String)noPermissionMessage);
        return this;
    }

    public String getNoPermissionMessage() {
        return this.noPermissionMessage;
    }
}

